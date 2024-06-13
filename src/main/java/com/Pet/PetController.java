package com.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PetController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PetService petService;

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestBody Pet pet) {
        logger.info("Erhaltene Daten: {} - {}", pet.getName(), pet.getType());

        if (pet.getName() == null || pet.getType() == null || pet.getName().isEmpty() || pet.getType().isEmpty()) {
            logger.warn("Name oder Typ des Haustiers sind leer.");
            return ResponseEntity.badRequest().body("Name und Typ des Haustiers dürfen nicht leer sein.");
        }

        try {
            // Save the pet in the database
            String insertPetQuery = "INSERT INTO application_pet (name, type, hunger, durst, energie, komfort, created_date, lastFed, lastWatered, lastSlept, lastPetted, lastShowered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertPetQuery, pet.getName(), pet.getType(), pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getCreatedDate(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered());

            return new ResponseEntity<>(pet, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Fehler bei der Erstellung des Haustiers", e);
            return ResponseEntity.status(500).body("Fehler bei der Erstellung des Haustiers");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        logger.info("Speichern der Daten für Haustier: {}", pet.getName());

        try {
            String updatePetQuery = "UPDATE application_pet SET hunger = ?, durst = ?, energie = ?, komfort = ?, lastFed = ?, lastWatered = ?, lastSlept = ?, lastPetted = ?, lastShowered = ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(updatePetQuery, pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered(), pet.getId());

            if (rowsAffected > 0) {
                logger.info("Haustierdaten erfolgreich gespeichert");
                return ResponseEntity.ok("Haustierdaten erfolgreich gespeichert");
            } else {
                logger.error("Fehler beim Speichern der Haustierdaten");
                return ResponseEntity.status(500).body("Fehler beim Speichern der Haustierdaten");
            }

        } catch (Exception e) {
            logger.error("Fehler beim Speichern der Haustierdaten", e);
            return ResponseEntity.status(500).body("Fehler beim Speichern der Haustierdaten");
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        try {
            String findTopPetsQuery = "SELECT * FROM application_pet ORDER BY created_date DESC";
            List<Pet> pets = jdbcTemplate.query(findTopPetsQuery, new PetRowMapper());

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Top-Haustiere", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Long id) {
        logger.info("Abrufen von Haustier mit ID: {}", id);

        try {
            String findPetByIdQuery = "SELECT * FROM application_pet WHERE id = ?";
            Pet pet = jdbcTemplate.queryForObject(findPetByIdQuery, new Object[]{id}, new PetRowMapper());

            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Haustiers", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden");
        }
    }
}