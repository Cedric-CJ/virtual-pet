<template>
  <div v-if="petData.name">
    <h1>{{ petData.name }}</h1>
    <p>Type: {{ petData.type }}</p>
    <p>Energie: {{ petData.energie }}</p>
    <div class="actions">
      <button @click="performAction('feed')">Füttern</button>
      <button @click="performAction('water')">Wasser geben</button>
      <button @click="performAction('sleep')">Schlafen</button>
      <button @click="performAction('pet')">Streicheln</button>
      <button @click="performAction('clean')">Duschen</button>
      <button @click="performAction('play')">Spielen</button>
    </div>
    <transition name="fade">
      <p v-if="actionText" class="action-text">{{ actionText }}</p>
    </transition>
  </div>
  <div v-else>
    <p>Lade Haustierdaten...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const petId = sessionStorage.getItem('petId');
const petData = ref({});
const actionText = ref('');

const fetchPetData = async () => {
  try {
    const response = await axios.get(`/api/pet/${petId}`);
    petData.value = response.data;
  } catch (error) {
    console.error('Fehler beim Laden der Haustierdaten:', error);
  }
};

const performAction = async (action) => {
  try {
    const response = await axios.post(`/api/pet/${petId}/${action}`);
    console.log(`${action} ausgeführt:`, response.data);
    updateActionText(action);
  } catch (error) {
    console.error('Fehler bei der Aktion:', error);
  }
};

const updateActionText = (action) => {
  const actions = {
    feed: 'Hunger +50',
    water: 'Durst +100',
    sleep: 'Energie aufgefüllt',
    pet: 'Komfort +10',
    clean: 'Komfort +100',
    play: 'Energie -10, Komfort +10'
  };
  actionText.value = actions[action];
  setTimeout(() => {
    actionText.value = '';
  }, 3000);
};

onMounted(fetchPetData);
</script>

<style scoped>
.actions button {
  margin: 10px;
  padding: 10px;
  border: 1px solid #ccc;
}

.action-text {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: #fff;
  background-color: #000;
  padding: 5px 10px;
  border-radius: 5px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active in <2.1.8 */ {
  opacity: 0;
}
</style>