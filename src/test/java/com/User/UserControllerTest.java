package com.User;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(1);

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Benutzer erfolgreich registriert", response.getBody());
    }

    @Test
    void testRegisterUserUsernameTaken() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Benutzername ist bereits vergeben.", response.getBody());
    }

    @Test
    void testLoginUserSuccess() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testuser");
        loginData.put("password", "password");

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn("hashedPassword");
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Anmeldung erfolgreich", response.getBody());
    }

    @Test
    void testLoginUserInvalidCredentials() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testuser");
        loginData.put("password", "password");

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn("hashedPassword");
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Ungültiger Benutzername oder Passwort.", response.getBody());
    }
}
