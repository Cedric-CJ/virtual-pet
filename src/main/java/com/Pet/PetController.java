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
    public ResponseEntity<?> createPet(@RequestBody Pet newPet) {
        logger.info("Received data: {} - {}", newPet.getName(), newPet.getType());

        if (newPet.getName() == null || newPet.getType() == null || newPet.getName().isEmpty() || newPet.getType().isEmpty()) {
            logger.warn("Name or Type is empty.");
            return ResponseEntity.badRequest().body("Name and Type cannot be empty.");
        }

        try {
            String insertPetQuery = "INSERT INTO application_pet (name, type, hunger, durst, energie, komfort, created_date, last_fed, last_watered, last_slept, last_petted, last_showered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = jdbcTemplate.update(insertPetQuery, newPet.getName(), newPet.getType(), newPet.getHunger(), newPet.getDurst(), newPet.getEnergie(), newPet.getKomfort(), newPet.getCreatedDate(), newPet.getLastFed(), newPet.getLastWatered(), newPet.getLastSlept(), newPet.getLastPetted(), newPet.getLastShowered());

            if (rowsAffected > 0) {
                logger.info("Pet successfully created");
                return ResponseEntity.ok("Pet successfully created");
            } else {
                logger.error("Pet could not be created");
                return ResponseEntity.status(500).body("Error creating pet");
            }

        } catch (Exception e) {
            logger.error("Error creating pet", e);
            return ResponseEntity.status(500).body("Error creating pet: " + e.getMessage());
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