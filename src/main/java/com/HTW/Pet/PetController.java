package com.HTW.Pet;

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
        logger.info("Empfangene Daten: {} - {} - {}", newPet.getUserId(), newPet.getName(), newPet.getType());

        if (newPet.getName() == null || newPet.getType() == null || newPet.getName().isEmpty() || newPet.getType().isEmpty() || newPet.getUserId() == null) {
            logger.warn("Name, Typ oder Benutzer-ID sind leer.");
            return ResponseEntity.badRequest().body("Name, Typ und Benutzer-ID dürfen nicht leer sein.");
        }

        try {
            Pet createdPet = petService.createPet(newPet.getType(), newPet.getName(), newPet.getUserId());
            logger.info("Haustier erfolgreich erstellt");
            return ResponseEntity.ok(createdPet);
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen des Haustiers", e);
            return ResponseEntity.status(500).body("Fehler beim Erstellen des Haustiers: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        logger.info("Daten für Haustier werden gespeichert: {}", pet);

        try {
            Pet savedPet = petService.savePet(pet);
            logger.info("Haustierdaten erfolgreich gespeichert");
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            logger.error("Fehler beim Speichern der Haustierdaten: ", e);
            return ResponseEntity.status(500).body("Fehler beim Speichern der Haustierdaten: " + e.getMessage());
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        try {
            List<Pet> pets = petService.getAllPets();
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Top-Haustiere", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{userId}/{name}")
    public ResponseEntity<?> getPetById(@PathVariable Long userId, @PathVariable String name) {
        logger.info("Haustier mit Benutzer-ID: {} und Name: {} wird abgerufen.", userId, name);

        try {
            Pet pet = petService.getPetDetails(userId, name);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Haustiers", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tier nicht gefunden");
        }
    }
}