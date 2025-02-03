<template>
  <div class="pet-creation">
    <div class="title-container">
      <h1 class="title left">C<br>r<br>e<br>a<br>t<br>e<br><br>y<br>o<br>u<br>r<br><br>p<br>e<br>t</h1>
      <h1 class="title right">C<br>r<br>e<br>a<br>t<br>e<br><br>y<br>o<br>u<br>r<br><br>p<br>e<br>t</h1>
    </div>
    <div class="content">
      <div class="pet-selection">
        <div class="pet-option1" @mouseenter="hoverPet('dog')" @mouseleave="leavePet" @click="selectPet('dog')">
          <img
              src="@/assets/pictures/dogpositiv.png"
              alt="Hund"
              :class="{ selected: petData.type === 'dog', hovering: hoveredPet === 'dog' }"
          />
        </div>
        <div class="pet-option2" @mouseenter="hoverPet('cat')" @mouseleave="leavePet" @click="selectPet('cat')">
          <img
              src="@/assets/pictures/catpositiv.png"
              alt="Katze"
              :class="{ selected: petData.type === 'cat', hovering: hoveredPet === 'cat' }"
          />
        </div>
      </div>
      <div class="input-container">
        <input
            id="petName"
            v-model="petData.name"
            placeholder="Your pet's name"
            required
            @input="handleInput"
            @keydown.enter="createPet"
            class="animated-input"
        />
      </div>
      <button
          v-if="petData.name"
          @click="createPet"
          :disabled="!petData.type || !petData.name || loading"
          class="glow-button"
      >
        <span v-if="!loading">Create</span>
        <span v-else class="loader"></span>
      </button>
      <div v-if="message" class="message" :class="{'error': messageType === 'error', 'success': messageType === 'success'}">
        {{ message }}
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useUserStore } from '@/store';
import dogBarkSound from '@/assets/sounds/Hund_kurz_jung.mp3';
import catMeowSound from '@/assets/sounds/Katze_kurz_jung.mp3';
const petData = ref({
  name: '',
  type: '',
  userId: ''
});
const router = useRouter();
const loading = ref(false);
const message = ref('');
const messageType = ref('');
const hoveredPet = ref<string | null>(null);
const store = useUserStore();
const dogBark = new Audio(dogBarkSound);
const catMeow = new Audio(catMeowSound);
onMounted(() => {
  petData.value.userId = store.userId;
});
const selectPet = (type: string) => {
  petData.value.type = type;
  if (type === 'dog') {
    dogBark.currentTime = 0;
    dogBark.play();
  }
  if (type === 'cat') {
    catMeow.currentTime = 0;
    catMeow.play();
  }
};
const hoverPet = (type: string) => {
  hoveredPet.value = type;
};
const leavePet = () => {
  hoveredPet.value = null;
};
const handleInput = () => {
  if (petData.value.name) {
    document.querySelector('button')?.classList.add('glow-button');
  } else {
    document.querySelector('button')?.classList.remove('glow-button');
  }
};
const createPet = async (event: Event) => {
  event.preventDefault();
  if (petData.value.name && petData.value.type && petData.value.userId) {
    loading.value = true;
    message.value = '';
    try {
      const response = await axios.post("https://virtual-pet-backend.onrender.com/api/checkAndCreate", petData.value);
      if (response.status === 200) {
        message.value = typeof response.data === 'string' ? response.data : 'Haustier erfolgreich erstellt!';
        messageType.value = typeof response.data === 'string' ? 'error' : 'success';
        if (messageType.value === 'success') {
          await router.push({ name: 'Pet' });
        }
      } else {
        message.value = 'Fehler beim Erstellen des Haustiers';
        messageType.value = 'error';
      }
    } catch (error: unknown) {
      message.value = axios.isAxiosError(error) ? error.response?.data || 'Fehler beim Erstellen' : 'Ein unbekannter Fehler ist aufgetreten';
      messageType.value = 'error';
    } finally {
      loading.value = false;
    }
  } else {
    message.value = 'Bitte alle Felder ausfüllen';
    messageType.value = 'error';
  }
};
</script>
<style>
@import url('https://fonts.googleapis.com/css2?family=Special+Elite&display=swap');

html, body {
  margin: 0;
  padding: 0;
  font-family: 'Special Elite', cursive;
}
.pet-creation {
  width: auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  text-align: center;
  font-size: calc(16px + 0.5vw);
  background: linear-gradient(-45deg, #c19a6b, #e6c27a, #d2a679, #a67c52);
  background-size: 400% 400%;
  animation: gradientMove 5s ease infinite;
}
@keyframes gradientMove {
  0% { background-position: 0 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}
.title-container {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 95%;
  display: flex;
  justify-content: space-between;
  pointer-events: none;
}
.title {
  font-size: 2rem;
  opacity: 0.5;
  text-transform: uppercase;
  line-height: 1.9rem;
  text-align: center;
}
.pet-option1 img.selected, .pet-option2 img.selected {
  transform: scale(1.2);
  filter: drop-shadow(0 0 15px rgba(255, 0, 150, 0.8)) drop-shadow(0 0 30px rgba(0, 255, 255, 0.8));
  animation: rainbow-border 2s linear infinite;
}
.input-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.animated-input {
  width: 280px;
  padding: 12px;
  font-size: 1.2rem;
  text-align: center;
  border: 2px solid transparent;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
  color: white;
  transition: 0.3s ease-in-out;
  outline: none;
  position: relative;
}
.glow-button {
  padding: 12px 25px;
  font-size: 1.2rem;
  font-weight: bold;
  text-transform: uppercase;
  color: white;
  background: linear-gradient(90deg, #ff0000, #ff7300, #ffeb00, #00ff00, #00ffff, #0000ff, #8000ff);
  background-size: 400% 400%;
  border: none;
  border-radius: 50px;
  box-shadow: 0 0 10px rgb(255, 255, 255);
  transition: all 0.3s ease-in-out;
  cursor: pointer;
  animation: rainbowAnimation 15s linear infinite;
}
.glow-button:disabled {
  filter: grayscale(100%);
  opacity: 0;
  cursor: not-allowed;
}
@keyframes rainbowAnimation {
  0% { background-position: 0 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0 50%; }
}
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.pet-selection {
  display: flex;
  justify-content: center;
  gap: 10vw;
  margin-bottom: 20px;
}
.pet-option1, .pet-option2 {
  cursor: pointer;
  flex: 1 1 100px;
  max-width: 20vw;
  transition: transform 0.3s ease-in-out;
}
.pet-option1 img, .pet-option2 img {
  width: 100%;
  transition: transform 0.3s, filter 0.3s;
}
.pet-option1 img.selected, .pet-option2 img.selected {
  transform: scale(1.2);
  filter: drop-shadow(0 0 25px #ffffff);
}
.pet-option1 img.hovering {
  transform: scale(1.1) rotate(-10deg);
}
.pet-option2 img.hovering {
  transform: scale(1.1) rotate(10deg);
}
</style>