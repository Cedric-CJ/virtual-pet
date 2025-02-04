package com.HTW.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("Pet data received: " + pet.toString());

        try {
            Pet savedPet = petService.savePet(pet);
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error by saving pet data: " + e.getMessage());
        }
    }

    @PostMapping("/updateStats")
    @Transactional
    public ResponseEntity<?> updatePetStats(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf((Integer) request.get("userId"));
        String name = (String) request.get("name");
        Map<String, Integer> stats = (Map<String, Integer>) request.get("stats");

        System.out.println("Statistics received: " + stats.toString());

        try {
            petService.updatePetStats(userId, name, stats);
            return ResponseEntity.ok("Statistics updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error by updating statistics: " + e.getMessage());
        }
    }

    @PostMapping("/updateLastActions")
    @Transactional
    public ResponseEntity<?> updateLastActions(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf((Integer) request.get("userId"));
        String name = (String) request.get("name");
        Map<String, String> lastActions = (Map<String, String>) request.get("lastActions");

        System.out.println("Recent actions received: " + lastActions.toString());

        try {
            petService.updateLastActions(userId, name, lastActions);
            return ResponseEntity.ok("Last actions updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error by updating recent actions: " + e.getMessage());
        }
    }

    @PostMapping("/checkAndCreate")
    public ResponseEntity<?> checkAndCreatePet(@RequestBody Pet newPet) {
        try {
            boolean petExists = petService.doesPetExist(newPet.getUserId(), newPet.getName());
            if (petExists) {
                return ResponseEntity.status(400).body("Pet name has already been used please use a different one");
            } else {
                Pet createdPet = petService.createPetInternal(newPet.getType(), newPet.getName(), newPet.getUserId());
                return ResponseEntity.ok(createdPet);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errors by verifying or creating the pet: " + e.getMessage());
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> deletePet(@RequestBody PetRequest petRequest) {
        try {
            petService.deletePet(petRequest.getUserId(), petRequest.getUsername());
            return ResponseEntity.ok("Animal successfully deleted and moved to the dead table");
        } catch (PetService.PetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}