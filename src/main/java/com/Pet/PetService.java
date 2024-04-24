package com.Pet;

import com.Pet.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet createPet(String type, String name, String asciiArt) {
        Pet newPet = new Pet(name, type, "Generierte ASCII Art"); // 'Generierte ASCII Art' kann durch eine tatsächliche ASCII Art ersetzt werden
        return petRepository.save(newPet);
    }

    public Pet getPetDetails(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Haustier nicht gefunden: " + petId));
    }

    public void essen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.essengeben();
        petRepository.save(pet);
    }

    public void trinken(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.wassergeben();
        petRepository.save(pet);
    }

    public void schlafen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.schlafen();
        petRepository.save(pet);
    }

    public void streicheln(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.streicheln();
        petRepository.save(pet);
    }

    public void spielen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.spielen();
        petRepository.save(pet);
    }

    public void duschen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.duschen();
        petRepository.save(pet);
    }
}