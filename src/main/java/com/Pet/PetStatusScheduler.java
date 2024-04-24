package com.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetStatusScheduler {
    @Autowired
    private PetRepository petRepository;

    // Diese Methode wird jede Stunde aufgerufen
    @Scheduled(fixedRate = 3600000) // 3600000 Millisekunden = 1 Stunde
    public void updatePetStatuses() {
        List<Pet> pets = petRepository.findAll();
        for (Pet pet : pets) {
            pet.tick();
            petRepository.save(pet);
        }
    }
}