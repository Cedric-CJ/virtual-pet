<template>
  <div v-if="petData.name">
    <h1>{{ petData.name }}</h1>
    <p>Type: {{ petData.type }}</p>
    <p>Energie: {{ petData.energie }}</p>
    <button @click="feedPet">Füttern</button>
    <button @click="waterPet">Wasser geben</button>
    <!-- Weitere Aktionen -->
  </div>
  <div v-else>
    <p>Lade Haustierdaten...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const petId = sessionStorage.getItem('petId');  // Abrufen der Pet ID aus dem sessionStorage

const petData = ref({});

const fetchPetData = async () => {
  try {
    const response = await axios.get(`/api/pet/${petId}`);
    petData.value = response.data;
  } catch (error) {
    console.error('Fehler beim Laden der Haustierdaten:', error);
  }
};

onMounted(fetchPetData);
</script>
