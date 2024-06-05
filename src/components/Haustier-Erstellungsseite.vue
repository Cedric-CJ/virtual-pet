<template>
  <div class="pet-creation">
    <div class="h1">
      <h1>Erstelle dein Haustier</h1>
    </div>
    <div class="pet-selection">
      <div class="pet-option" @click="selectPet('dog')">
        <img
            src="@/assets/dogpositiv.png"
            alt="Hund"
            :class="{ selected: petData.type === 'dog' }"
        />
      </div>
      <div class="pet-option" @click="selectPet('cat')">
        <img
            src="@/assets/catpositiv.png"
            alt="Katze"
            :class="{ selected: petData.type === 'cat' }"
        />
      </div>
    </div>
    <input id="petName" v-model="petData.name" placeholder="Name des Haustieres" required @input="handleInput">
    <button @click="createPet" :disabled="!petData.type || !petData.name" :class="{ 'glow-button': petData.name }">Erstellen</button>
    <div class="explosion" ref="explosion"></div>
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
const explosion = ref(null);

const selectPet = (type) => {
  petData.value.type = type;
};

const handleInput = (event) => {
  if (event.target.value) {
    document.querySelector('button').classList.add('glow-button');
  } else {
    document.querySelector('button').classList.remove('glow-button');
  }
};

const createPet = async (event) => {
  if (petData.value.name && petData.value.type) {
    try {
      const response = await axios.post('http://localhost:8080/api/pet/create', petData.value);
      if (response.status === 201) {
        // Trigger Explosion Animation
        const explosionElement = explosion.value;
        explosionElement.style.left = `${event.clientX - (explosionElement.clientWidth / 2)}px`;
        explosionElement.style.top = `${event.clientY - (explosionElement.clientHeight / 2)}px`;
        explosionElement.classList.add('explosion-active');

        // Wait for the animation to finish before routing
        setTimeout(() => {
          router.push({ name: '/pet', params: { petData: JSON.stringify(response.data) } });
        }, 500); // The duration of the explosion animation
      }
    } catch (error) {
      console.error('Fehler beim Erstellen des Haustiers:', error);
    }
  }
};
</script>
<style scoped>
.pet-creation {
  max-width: 100%;
  margin: auto;
  padding: 20px;
  text-align: center;
  font-size: calc(16px + 0.5vw);
}

.pet-selection {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.pet-option {
  position: relative;
  cursor: pointer;
  flex: 1 1 100px;
  max-width: 150px;
}

.pet-option img {
  width: 100%;
  transition: transform 0.3s, filter 0.3s;
}

.pet-option img.selected {
  transform: scale(1.1);
  filter: drop-shadow(0 0 10px #4CAF50);
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

button.glow-button {
  box-shadow: 0 0 10px 2px rgba(0, 255, 0, 0.6);
}

@keyframes explosion {
  0% {
    opacity: 1;
    transform: scale(0);
  }
  100% {
    opacity: 0;
    transform: scale(3);
  }
}

.explosion {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background: radial-gradient(circle, rgba(255, 255, 0, 1) 0%, rgba(255, 0, 0, 1) 50%, rgba(0, 0, 0, 0) 70%);
  opacity: 0;
  pointer-events: none;
}

.explosion-active {
  animation: explosion 0.5s ease-out forwards;
}
</style>