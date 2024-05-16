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
    public Pet createPet(String type, String name) {
        Pet newPet = new Pet(name, type);
        return petRepository.save(newPet);
    }

    public Pet getPetDetails(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Haustier nicht gefunden: " + petId));
    }

    @Transactional
    public void essen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.essengeben();
        petRepository.save(pet);
    }

    @Transactional
    public void trinken(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.wassergeben();
        petRepository.save(pet);
    }

    @Transactional
    public void schlafen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.schlafen();
        petRepository.save(pet);
    }

    @Transactional
    public void streicheln(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.streicheln();
        petRepository.save(pet);
    }

    @Transactional
    public void spielen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.spielen();
        petRepository.save(pet);
    }

    @Transactional
    public void duschen(Long petId) {
        Pet pet = getPetDetails(petId);
        pet.duschen();
        petRepository.save(pet);
    }
}