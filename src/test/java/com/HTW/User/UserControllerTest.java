package com.HTW.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    // Erstellen eines Mock-Objekts für JdbcTemplate
    @Mock
    private JdbcTemplate jdbcTemplate;

    // Erstellen eines Mock-Objekts für BCryptPasswordEncoder
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Erstellen einer Instanz von UserController und Injizieren der Mock-Objekte
    @InjectMocks
    private UserController userController;

    // Diese Methode wird vor jedem Test ausgeführt, um die Mocks zu initialisieren
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testet die erfolgreiche Registrierung eines Benutzers
    @Test
    public void testRegisterUser_Success() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("testUserSuccess");
        newUser.setPassword("testPass");

        // Sicherstellen, dass der Benutzername nicht existiert
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        // Passwort-Hashing simulieren
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPass");

        // Einfügen des neuen Benutzers simulieren
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        // Benutzer-ID abrufen simulieren
        when(jdbcTemplate.queryForObject(eq("SELECT id FROM application_user WHERE username = ?"), any(Object[].class), eq(Integer.class))).thenReturn(1);

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<String, Object>) response.getBody()).containsKey("message"));
        assertTrue(((Map<String, Object>) response.getBody()).containsKey("userId"));
    }

    // Testet den Fall, dass der Benutzername bereits vergeben ist
    @Test
    public void testRegisterUser_UsernameTaken() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("testUserTaken");
        newUser.setPassword("testPass");

        // Simulieren, dass der Benutzername bereits existiert
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Benutzername ist bereits vergeben.", response.getBody());
    }

    // Testet den Fall, dass Benutzername oder Passwort leer sind
    @Test
    public void testRegisterUser_EmptyFields() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("");
        newUser.setPassword("");

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Benutzername und Passwort dürfen nicht leer sein.", response.getBody());
    }

    // Testet die erfolgreiche Anmeldung eines Benutzers
    @Test
    public void testLoginUser_Success() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testUserLoginSuccess");
        loginData.put("password", "testPass");

        Map<String, Object> userRecord = new HashMap<>();
        userRecord.put("id", 1L);
        userRecord.put("username", "testUserLoginSuccess");
        userRecord.put("password", "hashedPass");

        // Benutzer finden und Passwort-Hashing simulieren
        when(jdbcTemplate.queryForMap(anyString(), any(Object[].class))).thenReturn(userRecord);
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Überprüfen, ob der Benutzer ein Haustier hat
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<String, Object>) response.getBody()).containsKey("message"));
        assertTrue(((Map<String, Object>) response.getBody()).containsKey("userId"));
        assertTrue(((Map<String, Object>) response.getBody()).containsKey("hasPet"));
    }

    // Testet den Fall, dass die Anmeldedaten ungültig sind
    @Test
    public void testLoginUser_InvalidCredentials() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testUserLoginInvalid");
        loginData.put("password", "wrongPass");

        Map<String, Object> userRecord = new HashMap<>();
        userRecord.put("id", 1L);
        userRecord.put("username", "testUserLoginInvalid");
        userRecord.put("password", "hashedPass");

        // Benutzer finden und ungültiges Passwort simulieren
        when(jdbcTemplate.queryForMap(anyString(), any(Object[].class))).thenReturn(userRecord);
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Ungültiger Benutzername oder Passwort.", response.getBody());
    }

    // Testet den Fall, dass der Benutzer nicht gefunden wird
    @Test
    public void testLoginUser_UserNotFound() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "unknownUser");
        loginData.put("password", "testPass");

        // Simulieren, dass der Benutzer nicht gefunden wird
        when(jdbcTemplate.queryForMap(anyString(), any(Object[].class))).thenThrow(new org.springframework.dao.IncorrectResultSizeDataAccessException(1));

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Ungültiger Benutzername oder Passwort.", response.getBody());
    }
}