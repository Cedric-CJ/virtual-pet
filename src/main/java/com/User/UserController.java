package com.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://virtual-pet-bcky.onrender.com"})
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @CrossOrigin
    @PostMapping("/api/registration")
    public ResponseEntity<?> registerUser(@RequestBody ApplicationUser newUser) {
        logger.info("Erhaltene Daten: {} - {}", newUser.getUsername(), newUser.getPassword());

        if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty()) {
            logger.warn("Benutzername oder Passwort sind leer.");
            return ResponseEntity.badRequest().body("Benutzername und Passwort dürfen nicht leer sein.");
        }

        try {
            String checkUserQuery = "SELECT COUNT(*) FROM application_user WHERE username = ?";
            int count = jdbcTemplate.queryForObject(checkUserQuery, new Object[]{newUser.getUsername()}, Integer.class);

            if (count > 0) {
                logger.warn("Benutzername ist bereits vergeben.");
                return ResponseEntity.badRequest().body("Benutzername ist bereits vergeben.");
            }

            String hashedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
            String insertUserQuery = "INSERT INTO application_user (username, password) VALUES (?, ?)";
            int rowsAffected = jdbcTemplate.update(insertUserQuery, newUser.getUsername(), hashedPassword);

            if (rowsAffected > 0) {
                logger.info("Benutzer erfolgreich registriert");
                return ResponseEntity.ok("Benutzer erfolgreich registriert");
            } else {
                logger.error("Benutzer konnte nicht registriert werden");
                return ResponseEntity.status(500).body("Fehler bei der Registrierung");
            }

        } catch (Exception e) {
            logger.error("Fehler bei der Registrierung", e);
            return ResponseEntity.status(500).body("Fehler bei der Registrierung");
        }
    }

    @CrossOrigin
    @PostMapping("/api/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            String findUserQuery = "SELECT password FROM application_user WHERE username = ?";
            String storedHashedPassword = jdbcTemplate.queryForObject(findUserQuery, new Object[]{username}, String.class);

            if (storedHashedPassword == null || !bCryptPasswordEncoder.matches(password, storedHashedPassword)) {
                logger.warn("Ungültiger Benutzername oder Passwort.");
                return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
            }

            // Überprüfen, ob der Benutzer bereits ein Haustier hat
            String checkPetQuery = "SELECT COUNT(*) FROM application_pet WHERE username = ?";
            int petCount = jdbcTemplate.queryForObject(checkPetQuery, new Object[]{username}, Integer.class);

            if (petCount > 0) {
                return ResponseEntity.ok("Anmeldung erfolgreich und Benutzer hat bereits ein Haustier");
            }

            logger.info("Anmeldung erfolgreich");
            return ResponseEntity.ok("Anmeldung erfolgreich");

        } catch (Exception e) {
            logger.error("Fehler bei der Anmeldung", e);
            return ResponseEntity.status(500).body("Ungültiger Benutzername oder Passwort.");
        }
    }
}