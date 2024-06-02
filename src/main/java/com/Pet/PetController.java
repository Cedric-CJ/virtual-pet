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
@RequestMapping("/api/pet")
public class PetController {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetService petService;

    @Autowired
    public UserRepository userRepository;

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        try {
            Pet pet = petService.getPetDetails(id);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @CrossOrigin
    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestBody Pet pet) {
        try {
            Pet newPet = petService.createPet(pet.getType(), pet.getName());
            return new ResponseEntity<>(newPet, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @CrossOrigin
    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("User", new ApplicationUser());
        return "register";
    }
    @CrossOrigin
    @PostMapping("/registration_process")
    public String handleRegistrationRequest(@RequestBody ApplicationUser applicationUser) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
        applicationUser.setPassword(encodedPassword);
        userRepository.save(applicationUser);
        return "registrationSuccessful";
    }
    @CrossOrigin
    @GetMapping("/pet")
    public ResponseEntity<?> getPetDetails(@RequestParam Long petId) {
        try {
            Pet pet = petService.getPetDetails(petId);
            return ResponseEntity.ok().body(Map.of("status", "success", "data", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @CrossOrigin
    @GetMapping("/{name}")
    public ResponseEntity<?> getPet(@PathVariable Long petId) {
        try{
            Pet pet = petService.getPetDetails(petId);
            return ResponseEntity.ok().body(Map.of("status", "success", "data", pet));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/feed")
    public ResponseEntity<String> essen(@PathVariable Long petId) {
        try {
            petService.essen(petId);
            return ResponseEntity.ok("Haustier gefüttert: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/water")
    public ResponseEntity<String> trinken(@PathVariable Long petId) {
        try {
            petService.trinken(petId);
            return ResponseEntity.ok("Wasser gegeben an: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/sleep")
    public ResponseEntity<String> schlafen(@PathVariable Long petId) {
        try {
            petService.schlafen(petId);
            return ResponseEntity.ok("Haustier schläft jetzt: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/pet")
    public ResponseEntity<String> streicheln(@PathVariable Long petId) {
        try {
            petService.streicheln(petId);
            return ResponseEntity.ok("Haustier gestreichelt: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/clean")
    public ResponseEntity<String> duschen(@PathVariable Long petId) {
        try {
            petService.duschen(petId);
            return ResponseEntity.ok("Haustier geduscht: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @PostMapping("/{name}/play")
    public ResponseEntity<String> spielen(@PathVariable Long petId) {
        try {
            petService.spielen(petId);
            return ResponseEntity.ok("Haustier spielt: " + petId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Haustier nicht gefunden: " + petId);
        }
    }
    @CrossOrigin
    @GetMapping("/top")
    public ResponseEntity<List<Pet>> getTopPets() {
        List<Pet> pets = petRepository.findAllByOrderByCreatedDateDesc();
        return ResponseEntity.ok(pets);
    }
}