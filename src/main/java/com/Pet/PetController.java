package com.Pet;

import com.User.ApplicationUser;
import com.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://virtual-pet-bcky.onrender.com"})
public class PetController {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetService petService;

    @Autowired
    public UserRepository userRepository;

    @CrossOrigin
    @GetMapping("/api/pet/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        try {
            Pet pet = petService.getPetDetails(id);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @CrossOrigin
    @PostMapping("/api/pet/create")
    public ResponseEntity<?> createPet(@RequestBody Pet pet) {
        try {
            Pet newPet = petService.createPet(pet.getType(), pet.getName());
            return new ResponseEntity<>(newPet, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin
    @PostMapping("/api/pet/save")
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        try {
            petRepository.save(pet);
            return ResponseEntity.ok("Pet data saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save pet data");
        }
    }

    @CrossOrigin
    @GetMapping("/api/pet/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        List<Pet> pets = petRepository.findAllByOrderByCreatedDateDesc();
        return ResponseEntity.ok(pets);
    }
}