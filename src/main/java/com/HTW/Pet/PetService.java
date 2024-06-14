package com.HTW.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    private static final int STATS_DECREASE_RATE = 5; // Stats decrease by 5 every hour

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional
    public Pet createPet(String type, String name, Long userId) {
        Pet newPet = new Pet();
        newPet.setType(type);
        newPet.setName(name);
        newPet.setUserId(userId);

        // Initialize default stats
        newPet.setHunger(5);
        newPet.setDurst(5);
        newPet.setEnergie(5);
        newPet.setKomfort(5);
        newPet.setCreatedDate(LocalDate.now());

        // Initialize last* fields with the current time
        LocalDateTime now = LocalDateTime.now();
        newPet.setLastFed(now);
        newPet.setLastWatered(now);
        newPet.setLastSlept(now);
        newPet.setLastPetted(now);
        newPet.setLastShowered(now);

        return petRepository.save(newPet);
    }

    public Pet getPetDetails(Long userId, String name) {
        PetId petId = new PetId(userId, name);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Haustier nicht gefunden: " + userId + " " + name));

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
    public Pet savePet(Pet pet) {
        // Find existing pet to ensure the created_date is not overwritten
        PetId petId = new PetId(pet.getUserId(), pet.getName());
        Pet existingPet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Haustier nicht gefunden: " + petId));

        // Preserve the original createdDate
        pet.setCreatedDate(existingPet.getCreatedDate());

        return petRepository.save(pet);
    }
}