package com.HTW.Pet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PetControllerTest {

    @InjectMocks
    private PetController petController; // Erstellt eine Instanz von PetController und injiziert gemockte Abhängigkeiten

    @Mock
    private PetService petService; // Mockt die PetService Abhängigkeit

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialisiert die Mock-Objekte vor jedem Test
    }

    @Test
    void testSavePet() {
        Pet pet = new Pet(); // Erstellt ein neues Pet-Objekt
        pet.setName("Bello");
        pet.setType("Dog");

        when(petService.savePet(any(Pet.class))).thenReturn(pet); // Definiert das Verhalten des gemockten petService.savePet()

        ResponseEntity<?> response = petController.savePet(pet); // Ruft die savePet Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertNotNull(response.getBody()); // Überprüft, ob die Antwort nicht null ist
    }

    @Test
    void testUpdatePetStats() {
        Map<String, Object> request = new HashMap<>(); // Erstellt ein neues Request-Objekt
        request.put("userId", 1);
        request.put("name", "Bello");
        request.put("stats", new HashMap<String, Integer>());

        ResponseEntity<?> response = petController.updatePetStats(request); // Ruft die updatePetStats Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertEquals("Statistiken erfolgreich aktualisiert", response.getBody()); // Überprüft, ob die Antwortnachricht korrekt ist
    }

    @Test
    void testUpdateLastActions() {
        Map<String, Object> request = new HashMap<>(); // Erstellt ein neues Request-Objekt
        request.put("userId", 1);
        request.put("name", "Bello");
        request.put("lastActions", new HashMap<String, String>());

        ResponseEntity<?> response = petController.updateLastActions(request); // Ruft die updateLastActions Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertEquals("Letzte Aktionen erfolgreich aktualisiert", response.getBody()); // Überprüft, ob die Antwortnachricht korrekt ist
    }

    @Test
    void testCheckAndCreatePet() {
        Pet newPet = new Pet(); // Erstellt ein neues Pet-Objekt
        newPet.setName("Bello");
        newPet.setType("Dog");
        newPet.setUserId(1L);

        when(petService.doesPetExist(any(Long.class), eq("Bello"))).thenReturn(false); // Definiert das Verhalten des gemockten petService.doesPetExist()
        when(petService.createPetInternal(eq("Dog"), eq("Bello"), any(Long.class))).thenReturn(newPet); // Definiert das Verhalten des gemockten petService.createPetInternal()

        ResponseEntity<?> response = petController.checkAndCreatePet(newPet); // Ruft die checkAndCreatePet Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertNotNull(response.getBody()); // Überprüft, ob die Antwort nicht null ist
    }

    @Test
    void testGetAllPets() {
        when(petService.getAllPets()).thenReturn(List.of(new Pet(), new Pet())); // Definiert das Verhalten des gemockten petService.getAllPets()

        ResponseEntity<List<Pet>> response = petController.getAllPets(); // Ruft die getAllPets Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertNotNull(response.getBody()); // Überprüft, ob die Antwort nicht null ist
    }

    @Test
    void testGetTopPets() {
        when(petService.getAllPetsInternal()).thenReturn(List.of(new Pet(), new Pet())); // Definiert das Verhalten des gemockten petService.getAllPetsInternal()

        ResponseEntity<List<Pet>> response = petController.getTopPets(); // Ruft die getTopPets Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertNotNull(response.getBody()); // Überprüft, ob die Antwort nicht null ist
    }

    @Test
    void testGetPetById() {
        PetRequest petRequest = new PetRequest(); // Erstellt ein neues PetRequest-Objekt
        petRequest.setUserId(1L);
        petRequest.setUsername("Bello");

        Pet pet = new Pet(); // Erstellt ein neues Pet-Objekt
        pet.setName("Bello");
        pet.setType("Dog");

        when(petService.getPetDetailsInternal(eq(1L), eq("Bello"))).thenReturn(pet); // Definiert das Verhalten des gemockten petService.getPetDetailsInternal()

        ResponseEntity<?> response = petController.getPetById(petRequest); // Ruft die getPetById Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertNotNull(response.getBody()); // Überprüft, ob die Antwort nicht null ist
    }

    @Test
    void testDeletePet() {
        PetRequest petRequest = new PetRequest(); // Erstellt ein neues PetRequest-Objekt
        petRequest.setUserId(1L);
        petRequest.setUsername("Bello");

        ResponseEntity<?> response = petController.deletePet(petRequest); // Ruft die deletePet Methode des Controllers auf
        assertEquals(200, response.getStatusCodeValue()); // Überprüft, ob der HTTP-Statuscode 200 ist
        assertEquals("Tier erfolgreich gelöscht und in die application_dead Tabelle verschoben", response.getBody()); // Überprüft, ob die Antwortnachricht korrekt ist
    }
}