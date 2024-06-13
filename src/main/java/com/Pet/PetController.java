package com.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PetController {

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
            Pet createdPet = petService.createPet(newPet.getType(), newPet.getName(), newPet.getUsername());
            logger.info("Pet successfully created");
            return ResponseEntity.ok(createdPet);
        } catch (Exception e) {
            logger.error("Error creating pet", e);
            return ResponseEntity.status(500).body("Error creating pet: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        logger.info("Saving data for pet: {}", pet);

        try {
            Pet savedPet = petService.savePet(pet);
            logger.info("Pet data successfully saved");
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            logger.error("Error saving pet data: ", e);
            return ResponseEntity.status(500).body("Error saving pet data: " + e.getMessage());
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        try {
            List<Pet> pets = petService.getAllPets();
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
            Pet pet = petService.getPetDetails(username, name);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            logger.error("Error retrieving pet", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet not found");
        }
    }
}