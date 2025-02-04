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
        logger.info("Data received: {} - {}", newUser.getUsername(), newUser.getPassword());

        if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty()) {
            logger.warn("Username or password are empty.");
            return ResponseEntity.badRequest().body("Username and password cannot be empty.");
        }

        try {
            String checkUserQuery = "SELECT COUNT(*) FROM v_pet.user WHERE username = ?";
            int count = jdbcTemplate.queryForObject(checkUserQuery, new Object[]{newUser.getUsername()}, Integer.class);

            if (count > 0) {
                logger.warn("Username is already taken.");
                return ResponseEntity.badRequest().body("Username is already taken.");
            }

            String hashedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());
            String insertUserQuery = "INSERT INTO v_pet.user (username, password) VALUES (?, ?)";
            int rowsAffected = jdbcTemplate.update(insertUserQuery, newUser.getUsername(), hashedPassword);

            if (rowsAffected > 0) {
                String getUserIdQuery = "SELECT id FROM v_pet.user WHERE username = ?";
                Integer userId = jdbcTemplate.queryForObject(getUserIdQuery, new Object[]{newUser.getUsername()}, Integer.class);
                logger.info("User successfully registered with ID: {}", userId);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User registered successfully");
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                logger.error("User could not be registered");
                return ResponseEntity.status(500).body("Registration error");
            }

        } catch (Exception e) {
            logger.error("Registration error", e);
            return ResponseEntity.status(500).body("Registration error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        logger.info("Login attempt for username: {}", username);

        try {
            String findUserQuery = "SELECT id, username, password FROM v_pet.user WHERE username = ?";
            Map<String, Object> userRecord = jdbcTemplate.queryForMap(findUserQuery, username);

            Long userId = ((Number) userRecord.get("id")).longValue();
            String storedHashedPassword = (String) userRecord.get("password");
            String storedUsername = (String) userRecord.get("username");
            logger.debug("Stored hashed password for user {}: {}", username, storedHashedPassword);

            if (storedHashedPassword == null) {
                logger.warn("User not found: {}", username);
                return ResponseEntity.badRequest().body("Invalid username or password.");
            }

            if (!bCryptPasswordEncoder.matches(password, storedHashedPassword)) {
                logger.warn("Invalid password for user: {}", username);
                return ResponseEntity.badRequest().body("Invalid username or password.");
            }

            // Überprüfen, ob der Benutzer bereits ein Haustier hat
            String checkPetQuery = "SELECT COUNT(*) FROM v_pet.pet WHERE username = ?";
            int petCount = jdbcTemplate.queryForObject(checkPetQuery, new Object[]{username}, Integer.class);
            logger.debug("Pet count for user {}: {}", username, petCount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("userId", userId);
            response.put("username", storedUsername); // Benutzername hinzufügen
            response.put("hasPet", petCount > 0);

            logger.info("Registration successful and user already has a pet: {}", petCount > 0);
            return ResponseEntity.ok(response);

        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            logger.error("User not found or too many results for user: {}", username, e);
            return ResponseEntity.badRequest().body("Invalid username or password.");
        } catch (Exception e) {
            logger.error("User login error: " + username, e);
            return ResponseEntity.status(500).body("User login error: " + e.getMessage());
        }
    }

}