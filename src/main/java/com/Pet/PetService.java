package com.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional
    public Pet createPet(String type, String name, String username) {
        Pet newPet = new Pet();
        newPet.setType(type);
        newPet.setName(name);
        newPet.setUsername(username);
        return petRepository.save(newPet);
    }

    public Pet getPetDetails(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Haustier nicht gefunden: " + petId));
    }
}