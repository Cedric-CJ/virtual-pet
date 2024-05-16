<template>
  <div class="pet-creation">
    <h1>Erstelle dein Haustier</h1>
    <div class="pet-selection">
      <div class="pet-option" @click="selectPet('dog')">
        <img
          src="@/assets/dog/front.png"
          alt="Hund"
          :class="{ selected: petData.type === 'dog' }"
        />
      </div>
      <div class="pet-option" @click="selectPet('cat')">
        <img
          src="@/assets/cat/front.png"
          alt="Katze"
          :class="{ selected: petData.type === 'cat' }"
        />
      </div>
    </div>
    <input id="petName" v-model="petData.name" placeholder="Name des Haustieres" required>
    <button @click="createPet" :disabled="!petData.type || !petData.name">Erstellen</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const petData = ref({
  name: '',
  type: ''
});
const router = useRouter();

const selectPet = (type) => {
  petData.value.type = type;
};

const createPet = async () => {
  if (petData.value.name && petData.value.type) {
    try {
      const response = await axios.post('http://localhost:8080/api/pet/create', petData.value);
      if (response.status === 201) {
        router.push({ name: 'pet', params: { petData: JSON.stringify(response.data) } });
      }
    } catch (error) {
      console.error('Fehler beim Erstellen des Haustiers:', error);
    }
  }
};
</script>

<style scoped>
.pet-creation {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  text-align: center;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.pet-selection {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.pet-option {
  position: relative;
  cursor: pointer;
}

.pet-option img {
  max-width: 150px;
  transition: transform 0.3s, border 0.3s;
}

.pet-option img.selected {
  transform: scale(1.1);
  border: 3px solid #4CAF50;
  border-radius: 10px;
}

input[type="text"] {
  width: 70%;
  padding: 10px;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  width: 20%;
  padding: 10px;
  border: none;
  background-color: #4CAF50;
  color: #fff;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s;
  margin-left: 5%;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:not(:disabled):hover {
  background-color: #45a049;
}

.pet-slider:hover {
  opacity: 1;
}

.pet-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 25px;
  height: 25px;
  background: #4CAF50;
  cursor: pointer;
  border-radius: 50%;
}

.pet-slider::-moz-range-thumb {
  width: 25px;
  height: 25px;
  background: #4CAF50;
  cursor: pointer;
  border-radius: 50%;
}
</style>