package com.HTW.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody ApplicationUser newUser) {
        logger.info("Erhaltene Daten: {} - {}", newUser.getUsername(), newUser.getPassword());

        if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty()) {
            logger.warn("Benutzername oder Passwort sind leer.");
            return ResponseEntity.badRequest().body("Benutzername und Passwort dürfen nicht leer sein.");
        }

        try {
            String checkUserQuery = "SELECT COUNT(*) FROM user WHERE username = ?";
            int count = jdbcTemplate.queryForObject(checkUserQuery, new Object[]{newUser.getUsername()}, Integer.class);

            if (count > 0) {
                logger.warn("Benutzername ist bereits vergeben.");
                return ResponseEntity.badRequest().body("Benutzername ist bereits vergeben.");
            }

            String hashedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
            String insertUserQuery = "INSERT INTO user (username, password) VALUES (?, ?)";
            int rowsAffected = jdbcTemplate.update(insertUserQuery, newUser.getUsername(), hashedPassword);

            if (rowsAffected > 0) {
                String getUserIdQuery = "SELECT id FROM user WHERE username = ?";
                Integer userId = jdbcTemplate.queryForObject(getUserIdQuery, new Object[]{newUser.getUsername()}, Integer.class);
                logger.info("Benutzer erfolgreich registriert mit ID: {}", userId);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Benutzer erfolgreich registriert");
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                logger.error("Benutzer konnte nicht registriert werden");
                return ResponseEntity.status(500).body("Fehler bei der Registrierung");
            }

        } catch (Exception e) {
            logger.error("Fehler bei der Registrierung", e);
            return ResponseEntity.status(500).body("Fehler bei der Registrierung");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        logger.info("Login attempt for username: {}", username);

        try {
            String findUserQuery = "SELECT id, username, password FROM user WHERE username = ?";
            Map<String, Object> userRecord = jdbcTemplate.queryForMap(findUserQuery, username);

            Long userId = ((Number) userRecord.get("id")).longValue();
            String storedHashedPassword = (String) userRecord.get("password");
            String storedUsername = (String) userRecord.get("username");
            logger.debug("Stored hashed password for user {}: {}", username, storedHashedPassword);

            if (storedHashedPassword == null) {
                logger.warn("Benutzer nicht gefunden: {}", username);
                return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
            }

            if (!bCryptPasswordEncoder.matches(password, storedHashedPassword)) {
                logger.warn("Ungültiges Passwort für Benutzer: {}", username);
                return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
            }

            // Überprüfen, ob der Benutzer bereits ein Haustier hat
            String checkPetQuery = "SELECT COUNT(*) FROM pet WHERE username = ?";
            int petCount = jdbcTemplate.queryForObject(checkPetQuery, new Object[]{username}, Integer.class);
            logger.debug("Pet count for user {}: {}", username, petCount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Anmeldung erfolgreich");
            response.put("userId", userId);
            response.put("username", storedUsername); // Benutzername hinzufügen
            response.put("hasPet", petCount > 0);

            logger.info("Anmeldung erfolgreich und Benutzer hat bereits ein Haustier: {}", petCount > 0);
            return ResponseEntity.ok(response);

        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            logger.error("Benutzer nicht gefunden oder zu viele Ergebnisse für Benutzer: {}", username, e);
            return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
        } catch (Exception e) {
            logger.error("Fehler bei der Anmeldung für Benutzer: " + username, e);
            return ResponseEntity.status(500).body("Fehler bei der Anmeldung: " + e.getMessage());
        }
    }

}