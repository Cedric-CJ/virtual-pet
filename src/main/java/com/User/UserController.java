package com.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/api/registration")
    public ResponseEntity<?> registerUser(@RequestBody ApplicationUser newUser) {
        System.out.println("Erhaltene Daten: " + newUser.getUsername() + " - " + newUser.getPassword());

        String checkUserQuery = "SELECT COUNT(*) FROM application_user WHERE username = ?";
        int count = jdbcTemplate.queryForObject(checkUserQuery, new Object[]{newUser.getUsername()}, Integer.class);

        System.out.println("Benutzername überprüfen: " + count);
        if (count > 0) {
            return ResponseEntity.badRequest().body("Benutzername ist bereits vergeben.");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
        String insertUserQuery = "INSERT INTO application_user (username, password) VALUES (?, ?)";
        int rowsAffected = jdbcTemplate.update(insertUserQuery, newUser.getUsername(), hashedPassword);

        System.out.println("Zeilen betroffen: " + rowsAffected);

        // Überprüfen, ob der Benutzer bereits ein Haustier hat
        String checkPetQuery = "SELECT COUNT(*) FROM application_pet WHERE username = ?";
        int petCount = jdbcTemplate.queryForObject(checkPetQuery, new Object[]{newUser.getUsername()}, Integer.class);

        System.out.println("Haustier überprüfen: " + petCount);
        if (petCount > 0) {
            return ResponseEntity.ok("Benutzer erfolgreich registriert und hat bereits ein Haustier");
        }

        return ResponseEntity.ok("Benutzer erfolgreich registriert");
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        String findUserQuery = "SELECT password FROM application_user WHERE username = ?";
        try {
            String storedHashedPassword = jdbcTemplate.queryForObject(findUserQuery, new Object[]{username}, String.class);

            System.out.println("Gespeichertes gehashtes Passwort: " + storedHashedPassword);

            if (storedHashedPassword == null || !bCryptPasswordEncoder.matches(password, storedHashedPassword)) {
                return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
            }

            // Überprüfen, ob der Benutzer bereits ein Haustier hat
            String checkPetQuery = "SELECT COUNT(*) FROM application_pet WHERE username = ?";
            int petCount = jdbcTemplate.queryForObject(checkPetQuery, new Object[]{username}, Integer.class);

            if (petCount > 0) {
                return ResponseEntity.ok("Anmeldung erfolgreich und Benutzer hat bereits ein Haustier");
            }

            return ResponseEntity.ok("Anmeldung erfolgreich");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ungültiger Benutzername oder Passwort.");
        }
    }
}