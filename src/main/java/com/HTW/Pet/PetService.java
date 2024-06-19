package com.HTW.Pet;

import com.HTW.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int STATS_DECREASE_RATE = 5; // Stats gehen alle 5 Stunden um 1 runter

    @Transactional
    public Pet savePet(Pet pet) {
        // Überprüfen, ob das Haustier existiert
        String checkSql = "SELECT * FROM application_pet WHERE user_id = ? AND name = ?";
        Pet existingPet;
        try {
            existingPet = jdbcTemplate.queryForObject(checkSql, new Object[]{pet.getUserId(), pet.getName()}, (rs, rowNum) -> {
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
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Haustier nicht gefunden: " + pet.getUserId() + " " + pet.getName());
        }

        if (existingPet == null) {
            throw new RuntimeException("Haustier nicht gefunden: " + pet.getUserId() + " " + pet.getName());
        }

        System.out.println("Empfangene Haustierdaten: " + pet.toString());
        System.out.println("Aktualisiere Haustierdaten in der Datenbank: " + pet.toString());

        // Update SQL für die Haustierdaten
        String sql = "UPDATE application_pet SET hunger = ?, durst = ?, energie = ?, komfort = ?, created_date = ?, last_fed = ?, last_watered = ?, last_slept = ?, last_petted = ?, last_showered = ? WHERE user_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql, pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getCreatedDate(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered(), pet.getUserId(), pet.getName());

        System.out.println("Anzahl der aktualisierten Zeilen: " + rowsAffected);

        // Behalte die bestehenden name und type bei
        pet.setName(existingPet.getName());
        pet.setType(existingPet.getType());

        return pet;
    }

    @Transactional
    public void updatePetStats(Long userId, String petName, PetStats stats) {
        String sql = "UPDATE application_pet SET hunger = ?, durst = ?, energie = ?, komfort = ? WHERE user_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql, stats.getHunger(), stats.getDurst(), stats.getEnergie(), stats.getKomfort(), userId, petName);

        System.out.println("Anzahl der aktualisierten Zeilen für Statistiken: " + rowsAffected);
    }

    public Pet createPetInternal(String type, String name, Long userId) {
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

    public Pet getPetDetailsInternal(Long userId, String username) {
        String sql = "SELECT * FROM application_pet WHERE user_id = ? AND username = ?";
        Pet pet;
        try {
            pet = jdbcTemplate.queryForObject(sql, new Object[]{userId, username}, (rs, rowNum) -> {
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
        } catch (EmptyResultDataAccessException e) {
            throw new PetNotFoundException("Tier nicht gefunden", e);
        }

        updateStats(pet);
        return pet;
    }

    public boolean doesPetExist(Long userId, String name) {
        String sqlPet = "SELECT COUNT(*) FROM application_pet WHERE user_id = ? AND name = ?";
        Integer countPet = jdbcTemplate.queryForObject(sqlPet, new Object[]{userId, name}, Integer.class);

        String sqlDead = "SELECT COUNT(*) FROM application_dead WHERE user_id = ? AND name = ?";
        Integer countDead = jdbcTemplate.queryForObject(sqlDead, new Object[]{userId, name}, Integer.class);

        return (countPet != null && countPet > 0) || (countDead != null && countDead > 0);
    }

    public List<Pet> getAllPetsInternal() {
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
    public void deletePet(Long userId, String username) {
        Pet pet = getPetDetailsInternal(userId, username);
        moveToDeadTable(pet);
        deleteFromPetTable(userId, username);
    }

    private void moveToDeadTable(Pet pet) {
        String sql = "INSERT INTO application_dead (user_id, username, name, type, hunger, durst, energie, komfort, created_date, last_fed, last_watered, last_slept, last_petted, last_showered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, pet.getUserId(), pet.getUsername(), pet.getName(), pet.getType(), pet.getHunger(), pet.getDurst(), pet.getEnergie(), pet.getKomfort(), pet.getCreatedDate(), pet.getLastFed(), pet.getLastWatered(), pet.getLastSlept(), pet.getLastPetted(), pet.getLastShowered());
    }

    private void deleteFromPetTable(Long userId, String username) {
        String sql = "DELETE FROM application_pet WHERE user_id = ? AND username = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, username);

        if (rowsAffected == 0) {
            throw new RuntimeException("Haustier nicht gefunden oder konnte nicht gelöscht werden: " + userId + " " + username);
        }
    }

    private Pet updateStats(Pet pet) {
        LocalDateTime now = LocalDateTime.now();

        pet.setHunger(calculateStat(pet.getLastFed(), now, pet.getHunger()));
        pet.setDurst(calculateStat(pet.getLastWatered(), now, pet.getDurst()));
        pet.setEnergie(calculateStat(pet.getLastSlept(), now, pet.getEnergie()));
        pet.setKomfort(calculateStat(pet.getLastPetted(), now, pet.getKomfort()));
        return pet;
    }

    private int calculateStat(LocalDateTime lastAction, LocalDateTime now, int currentStat) {
        long hours = Duration.between(lastAction, now).toHours();
        return Math.max(currentStat - (int) (hours * STATS_DECREASE_RATE), 0);
    }

    public static class PetNotFoundException extends RuntimeException {
        public PetNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}