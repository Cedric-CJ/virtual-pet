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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        ApplicationUser newUser = new ApplicationUser();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Benutzer erfolgreich registriert", responseBody.get("message"));
        assertEquals(1, responseBody.get("userId"));
    }

    @Test
    public void testLoginUserSuccess() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testuser");
        loginData.put("password", "password");

        Map<String, Object> userRecord = new HashMap<>();
        userRecord.put("id", 1);
        userRecord.put("password", "hashedPassword");

        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenReturn(userRecord);
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        ResponseEntity<?> response = userController.loginUser(loginData);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Anmeldung erfolgreich", responseBody.get("message"));
        assertEquals(1, responseBody.get("userId"));
        assertTrue((Boolean) responseBody.get("hasPet"));
    }
}