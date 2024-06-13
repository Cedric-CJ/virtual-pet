package com.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // Set the current date and time for createdDate
        newPet.setCreatedDate(LocalDate.now());

        try {
            String insertPetQuery = "INSERT INTO application_pet (username, name, type, hunger, durst, energie, komfort, created_date, last_fed, last_watered, last_slept, last_petted, last_showered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int rowsAffected = jdbcTemplate.update(insertPetQuery, newPet.getUsername(), newPet.getName(), newPet.getType(), newPet.getHunger(), newPet.getDurst(), newPet.getEnergie(), newPet.getKomfort(), newPet.getCreatedDate(), newPet.getLastFed(), newPet.getLastWatered(), newPet.getLastSlept(), newPet.getLastPetted(), newPet.getLastShowered());

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
        logger.info("Saving data for pet: {}", pet);

        // Set the current date and time for last* fields
        LocalDateTime now = LocalDateTime.now();
        pet.setLastFed(now);
        pet.setLastWatered(now);
        pet.setLastSlept(now);
        pet.setLastPetted(now);
        pet.setLastShowered(now);

        // Log the current data for the given id
        try {
            String findPetByIdQuery = "SELECT * FROM application_pet WHERE username = ? AND name = ?";
            Pet existingPet = jdbcTemplate.queryForObject(findPetByIdQuery, new Object[]{pet.getUsername(), pet.getName()}, new PetRowMapper());
            logger.debug("Existing pet data: {}", existingPet);
        } catch (Exception e) {
            logger.error("Error finding pet with username {} and name {}: {}", pet.getUsername(), pet.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet not found with username " + pet.getUsername() + " and name " + pet.getName());
        }

        try {
            String updatePetQuery = "UPDATE application_pet SET hunger = ?, durst = ?, energie = ?, komfort = ?, last_fed = ?, last_watered = ?, last_slept = ?, last_petted = ?, last_showered = ? WHERE username = ? AND name = ?";
            int rowsAffected = jdbcTemplate.update(updatePetQuery, pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered(), pet.getUsername(), pet.getName());

            if (rowsAffected > 0) {
                logger.info("Pet data successfully saved");
                return ResponseEntity.ok("Pet data successfully saved");
            } else {
                logger.error("Error saving pet data: No rows affected");
                return ResponseEntity.status(500).body("Error saving pet data: No rows affected");
            }

        } catch (Exception e) {
            logger.error("Error saving pet data: ", e);
            return ResponseEntity.status(500).body("Error saving pet data: " + e.getMessage());
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        try {
            String findTopPetsQuery = "SELECT * FROM application_pet ORDER BY created_date DESC";
            List<Pet> pets = jdbcTemplate.query(findTopPetsQuery, new PetRowMapper());

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            logger.error("Error retrieving top pets", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{username}/{name}")
    public ResponseEntity<?> getPetById(@PathVariable String username, @PathVariable String name) {
        logger.info("Retrieving pet with username: {} and name: {}", username, name);

        try {
            String findPetByIdQuery = "SELECT * FROM application_pet WHERE username = ? AND name = ?";
            Pet pet = jdbcTemplate.queryForObject(findPetByIdQuery, new Object[]{username, name}, new PetRowMapper());

            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            logger.error("Error retrieving pet", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet not found");
        }
    }
}