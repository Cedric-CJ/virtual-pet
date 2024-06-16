package com.HTW.Pet;

import com.HTW.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    private static final int STATS_DECREASE_RATE = 5; // Stats decrease by 5 every hour

    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestBody Pet newPet) {
        logger.info("Empfangene Daten: {} - {} - {}", newPet.getUserId(), newPet.getName(), newPet.getType());

        if (newPet.getName() == null || newPet.getType() == null || newPet.getName().isEmpty() || newPet.getType().isEmpty() || newPet.getUserId() == null) {
            logger.warn("Name, Typ oder Benutzer-ID sind leer.");
            return ResponseEntity.badRequest().body("Name, Typ und Benutzer-ID dürfen nicht leer sein.");
        }

        try {
            Pet createdPet = createPetInternal(newPet.getType(), newPet.getName(), newPet.getUserId());
            logger.info("Haustier erfolgreich erstellt");
            return ResponseEntity.ok(createdPet);
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen des Haustiers", e);
            return ResponseEntity.status(500).body("Fehler beim Erstellen des Haustiers: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<?> savePet(@RequestBody Pet pet) {
        logger.info("Daten für Haustier werden gespeichert: {}", pet);

        try {
            Pet savedPet = savePetInternal(pet);
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
            List<Pet> pets = getAllPetsInternal();
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
            Pet pet = getPetDetailsInternal(userId, name);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Haustiers", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tier nicht gefunden");
        }
    }

    private List<Pet> getAllPetsInternal() {
        String sql = "SELECT * FROM application_pet";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Pet pet = new Pet();
            pet.setUserId(rs.getLong("user_id"));
            pet.setUsername(rs.getString("username"));
            pet.setName(rs.getString("name"));
            pet.setType(rs.getString("type"));
            pet.setHunger(rs.getInt("hunger"));
            pet.setDurst(rs.getInt("durst"));
            pet.setEnergie(rs.getInt("energie"));
            pet.setKomfort(rs.getInt("komfort"));
            pet.setCreatedDate(rs.getDate("created_date").toLocalDate());
            pet.setLastFed(rs.getTimestamp("last_fed").toLocalDateTime());
            pet.setLastWatered(rs.getTimestamp("last_watered").toLocalDateTime());
            pet.setLastSlept(rs.getTimestamp("last_slept").toLocalDateTime());
            pet.setLastPetted(rs.getTimestamp("last_petted").toLocalDateTime());
            pet.setLastShowered(rs.getTimestamp("last_showered").toLocalDateTime());
            return pet;
        });
    }

    @Transactional
    private Pet createPetInternal(String type, String name, Long userId) {
        Pet newPet = new Pet();
        newPet.setType(type);
        newPet.setName(name);
        newPet.setUserId(userId);

        String username = userRepository.findUsernameById(userId);
        newPet.setUsername(username);

        newPet.setHunger(5);
        newPet.setDurst(5);
        newPet.setEnergie(5);
        newPet.setKomfort(5);
        newPet.setCreatedDate(LocalDate.now());

        LocalDateTime now = LocalDateTime.now();
        newPet.setLastFed(now);
        newPet.setLastWatered(now);
        newPet.setLastSlept(now);
        newPet.setLastPetted(now);
        newPet.setLastShowered(now);

        return petRepository.save(newPet);
    }

    private Pet getPetDetailsInternal(Long userId, String name) {
        String sql = "SELECT * FROM application_pet WHERE user_id = ? AND name = ?";
        Pet pet = jdbcTemplate.queryForObject(sql, new Object[]{userId, name}, (rs, rowNum) -> {
            Pet p = new Pet();
            p.setUserId(rs.getLong("user_id"));
            p.setUsername(rs.getString("username"));
            p.setName(rs.getString("name"));
            p.setType(rs.getString("type"));
            p.setHunger(rs.getInt("hunger"));
            p.setDurst(rs.getInt("durst"));
            p.setEnergie(rs.getInt("energie"));
            p.setKomfort(rs.getInt("komfort"));
            p.setCreatedDate(rs.getDate("created_date").toLocalDate());
            p.setLastFed(rs.getTimestamp("last_fed").toLocalDateTime());
            p.setLastWatered(rs.getTimestamp("last_watered").toLocalDateTime());
            p.setLastSlept(rs.getTimestamp("last_slept").toLocalDateTime());
            p.setLastPetted(rs.getTimestamp("last_petted").toLocalDateTime());
            p.setLastShowered(rs.getTimestamp("last_showered").toLocalDateTime());
            return p;
        });

        updateStats(pet);
        return pet;
    }

    private void updateStats(Pet pet) {
        LocalDateTime now = LocalDateTime.now();

        pet.setHunger(calculateStat(pet.getLastFed(), now, pet.getHunger()));
        pet.setDurst(calculateStat(pet.getLastWatered(), now, pet.getDurst()));
        pet.setEnergie(calculateStat(pet.getLastSlept(), now, pet.getEnergie()));
        pet.setKomfort(calculateStat(pet.getLastPetted(), now, pet.getKomfort()));
    }

    private int calculateStat(LocalDateTime lastAction, LocalDateTime now, int currentStat) {
        long hours = Duration.between(lastAction, now).toHours();
        return Math.max(currentStat - (int) (hours * STATS_DECREASE_RATE), 0);
    }

    @Transactional
    private Pet savePetInternal(Pet pet) {
        String sql = "UPDATE application_pet SET hunger = ?, durst = ?, energie = ?, komfort = ?, created_date = ?, last_fed = ?, last_watered = ?, last_slept = ?, last_petted = ?, last_showered = ? WHERE user_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql, pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getCreatedDate(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered(), pet.getUserId(), pet.getName());

        if (rowsAffected == 0) {
            throw new RuntimeException("Haustier nicht gefunden: " + pet.getUserId() + " " + pet.getName());
        }

        return pet;
    }
}