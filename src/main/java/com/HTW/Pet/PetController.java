package com.HTW.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        System.out.println("Empfangene Haustierdaten: " + pet.toString());

        try {
            Pet savedPet = petService.savePet(pet);
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Speichern der Haustierdaten: " + e.getMessage());
        }
    }

    @PostMapping("/updateStats")
    @Transactional
    public ResponseEntity<?> updatePetStats(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf((Integer) request.get("userId"));
        String name = (String) request.get("name");
        Map<String, Integer> stats = (Map<String, Integer>) request.get("stats");

        System.out.println("Empfangene Statistiken: " + stats.toString());

        try {
            petService.updatePetStats(userId, name, stats);
            return ResponseEntity.ok("Statistiken erfolgreich aktualisiert");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Aktualisieren der Statistiken: " + e.getMessage());
        }
    }

    @PostMapping("/updateLastActions")
    @Transactional
    public ResponseEntity<?> updateLastActions(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf((Integer) request.get("userId"));
        String name = (String) request.get("name");
        Map<String, String> lastActions = (Map<String, String>) request.get("lastActions");

        System.out.println("Empfangene letzte Aktionen: " + lastActions.toString());

        try {
            petService.updateLastActions(userId, name, lastActions);
            return ResponseEntity.ok("Letzte Aktionen erfolgreich aktualisiert");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Aktualisieren der letzten Aktionen: " + e.getMessage());
        }
    }

    @PostMapping("/checkAndCreate")
    public ResponseEntity<?> checkAndCreatePet(@RequestBody Pet newPet) {
        try {
            boolean petExists = petService.doesPetExist(newPet.getUserId(), newPet.getName());
            if (petExists) {
                return ResponseEntity.status(400).body("Tiername wurde bereits verwendet benutze bitte einen anderen");
            } else {
                Pet createdPet = petService.createPetInternal(newPet.getType(), newPet.getName(), newPet.getUserId());
                return ResponseEntity.ok(createdPet);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler bei der Überprüfung oder Erstellung des Haustiers: " + e.getMessage());
        }
    }

    @GetMapping("/allPets")
    public ResponseEntity<List<Pet>> getAllPets() {
        try {
            List<Pet> pets = petService.getAllPets();
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        try {
            List<Pet> pets = petService.getAllPetsInternal();
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/userpet")
    public ResponseEntity<?> getPetById(@RequestBody PetRequest petRequest) {
        try {
            Pet pet = petService.getPetDetailsInternal(petRequest.getUserId(), petRequest.getUsername());
            return ResponseEntity.ok(pet);
        } catch (PetService.PetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tier nicht gefunden");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interner Serverfehler");
        }
    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> deletePet(@RequestBody PetRequest petRequest) {
        try {
            petService.deletePet(petRequest.getUserId(), petRequest.getUsername());
            return ResponseEntity.ok("Tier erfolgreich gelöscht und in die application_dead Tabelle verschoben");
        } catch (PetService.PetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tier nicht gefunden");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interner Serverfehler");
        }
    }
}