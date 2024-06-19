package com.HTW.Pet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PetControllerTest {

    // Erstellen eines Mock-Objekts für den PetService
    @Mock
    private PetService petService;

    // Erstellen einer Instanz von PetController und Injizieren der Mock-Objekte
    @InjectMocks
    private PetController petController;

    // Diese Methode wird vor jedem Test ausgeführt, um die Mocks zu initialisieren
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testet die erfolgreiche Speicherung eines Haustiers
    @Test
    public void testSavePet_Success() {
        Pet pet = new Pet();
        pet.setUserId(1L);
        pet.setName("Bobby");
        pet.setType("Dog");
        when(petService.savePet(any(Pet.class))).thenReturn(pet);

        ResponseEntity<?> response = petController.savePet(pet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pet, response.getBody());
    }

    // Testet das Fehlschlagen der Speicherung eines Haustiers
    @Test
    public void testSavePet_Failure() {
        Pet pet = new Pet();
        pet.setUserId(1L);
        pet.setName("Bobby");
        pet.setType("Dog");
        when(petService.savePet(any(Pet.class))).thenThrow(new RuntimeException("Save failed"));

        ResponseEntity<?> response = petController.savePet(pet);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fehler beim Speichern der Haustierdaten: Save failed", response.getBody());
    }

    // Testet das erfolgreiche Aktualisieren der Statistiken eines Haustiers
    @Test
    public void testUpdatePetStats_Success() {
        PetStats stats = new PetStats();
        doNothing().when(petService).updatePetStats(anyLong(), anyString(), any(PetStats.class));

        ResponseEntity<?> response = petController.updatePetStats(stats, 1L, "Bobby");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Statistiken erfolgreich aktualisiert", response.getBody());
    }

    // Testet das Fehlschlagen des Aktualisierens der Statistiken eines Haustiers
    @Test
    public void testUpdatePetStats_Failure() {
        PetStats stats = new PetStats();
        doThrow(new RuntimeException("Update failed")).when(petService).updatePetStats(anyLong(), anyString(), any(PetStats.class));

        ResponseEntity<?> response = petController.updatePetStats(stats, 1L, "Bobby");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fehler beim Aktualisieren der Statistiken: Update failed", response.getBody());
    }

    // Testet den Fall, dass ein Haustier bereits existiert
    @Test
    public void testCheckAndCreatePet_PetExists() {
        Pet newPet = new Pet();
        newPet.setUserId(1L);
        newPet.setName("Bobby");
        newPet.setType("Dog");
        when(petService.doesPetExist(anyLong(), anyString())).thenReturn(true);

        ResponseEntity<?> response = petController.checkAndCreatePet(newPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tiername wurde bereits verwendet benutze bitte einen anderen", response.getBody());
    }

    // Testet den erfolgreichen Fall der Erstellung eines neuen Haustiers
    @Test
    public void testCheckAndCreatePet_PetNotExists() {
        Pet newPet = new Pet();
        newPet.setUserId(1L);
        newPet.setName("Bobby");
        newPet.setType("Dog");
        when(petService.doesPetExist(anyLong(), anyString())).thenReturn(false);
        when(petService.createPetInternal(anyString(), anyString(), anyLong())).thenReturn(newPet);

        ResponseEntity<?> response = petController.checkAndCreatePet(newPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newPet, response.getBody());
    }

    // Testet das Fehlschlagen der Erstellung eines neuen Haustiers
    @Test
    public void testCheckAndCreatePet_Failure() {
        Pet newPet = new Pet();
        newPet.setUserId(1L);
        newPet.setName("Bobby");
        newPet.setType("Dog");
        when(petService.doesPetExist(anyLong(), anyString())).thenReturn(false);
        when(petService.createPetInternal(anyString(), anyString(), anyLong())).thenThrow(new RuntimeException("Create failed"));

        ResponseEntity<?> response = petController.checkAndCreatePet(newPet);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Fehler bei der Überprüfung oder Erstellung des Haustiers: Create failed", response.getBody());
    }

    // Testet das erfolgreiche Abrufen aller Haustiere
    @Test
    public void testGetAllPets_Success() {
        List<Pet> pets = Arrays.asList(new Pet(), new Pet());
        when(petService.getAllPets()).thenReturn(pets);

        ResponseEntity<List<Pet>> response = petController.getAllPets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
    }

    // Testet das Fehlschlagen des Abrufens aller Haustiere
    @Test
    public void testGetAllPets_Failure() {
        when(petService.getAllPets()).thenThrow(new RuntimeException("Fetch failed"));

        ResponseEntity<List<Pet>> response = petController.getAllPets();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Testet das erfolgreiche Abrufen der Top-Haustiere
    @Test
    public void testGetTopPets_Success() {
        List<Pet> pets = Arrays.asList(new Pet(), new Pet());
        when(petService.getAllPetsInternal()).thenReturn(pets);

        ResponseEntity<List<Pet>> response = petController.getTopPets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
    }

    // Testet das Fehlschlagen des Abrufens der Top-Haustiere
    @Test
    public void testGetTopPets_Failure() {
        when(petService.getAllPetsInternal()).thenThrow(new RuntimeException("Fetch failed"));

        ResponseEntity<List<Pet>> response = petController.getTopPets();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Testet das erfolgreiche Abrufen der Details eines bestimmten Haustiers
    @Test
    public void testGetPetById_Success() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        Pet pet = new Pet();
        pet.setUserId(1L);
        pet.setName("Bobby");
        pet.setType("Dog");
        when(petService.getPetDetailsInternal(anyLong(), anyString())).thenReturn(pet);

        ResponseEntity<?> response = petController.getPetById(petRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pet, response.getBody());
    }

    // Testet den Fall, dass das Haustier nicht gefunden wird
    @Test
    public void testGetPetById_PetNotFound() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        when(petService.getPetDetailsInternal(anyLong(), anyString())).thenThrow(new PetService.PetNotFoundException("Pet not found", null));

        ResponseEntity<?> response = petController.getPetById(petRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Tier nicht gefunden", response.getBody());
    }

    // Testet das Fehlschlagen des Abrufens der Details eines bestimmten Haustiers
    @Test
    public void testGetPetById_Failure() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        when(petService.getPetDetailsInternal(anyLong(), anyString())).thenThrow(new RuntimeException("Fetch failed"));

        ResponseEntity<?> response = petController.getPetById(petRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Interner Serverfehler", response.getBody());
    }

    // Testet das erfolgreiche Löschen eines Haustiers
    @Test
    public void testDeletePet_Success() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        doNothing().when(petService).deletePet(anyLong(), anyString());

        ResponseEntity<?> response = petController.deletePet(petRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tier erfolgreich gelöscht und in die application_dead Tabelle verschoben", response.getBody());
    }

    // Testet den Fall, dass das Haustier nicht gefunden wird
    @Test
    public void testDeletePet_PetNotFound() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        doThrow(new PetService.PetNotFoundException("Pet not found", null)).when(petService).deletePet(anyLong(), anyString());

        ResponseEntity<?> response = petController.deletePet(petRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Tier nicht gefunden", response.getBody());
    }

    // Testet das Fehlschlagen des Löschens eines Haustiers
    @Test
    public void testDeletePet_Failure() {
        PetRequest petRequest = new PetRequest();
        petRequest.setUserId(1L);
        petRequest.setUsername("testUser");
        doThrow(new RuntimeException("Delete failed")).when(petService).deletePet(anyLong(), anyString());

        ResponseEntity<?> response = petController.deletePet(petRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Interner Serverfehler", response.getBody());
    }
}