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
    <input
        id="petName"
        v-model="petData.name"
        placeholder="Name des Haustieres"
        required
        @input="handleInput"
        @keydown.enter="createPet"
    />
    <button
        @click="createPet"
        :disabled="!petData.type || !petData.name || loading"
        :class="{ 'glow-button': petData.name }"
    >
      <span v-if="!loading">Erstellen</span>
      <span v-else class="loader"></span>
    </button>
    <div class="explosion" ref="explosion"></div>
    <div v-if="message" class="message" :class="{'error': messageType === 'error', 'success': messageType === 'success'}">
      {{ message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useUserStore } from '@/store'; // Verwenden Sie den richtigen Pfad

const petData = ref({
  name: '',
  type: '',
  userId: ''
});
const router = useRouter();
const explosion = ref(null);
const loading = ref(false);
const message = ref('');
const messageType = ref('');
const store = useUserStore();

onMounted(() => {
  petData.value.userId = store.getUserId;
  console.log('UserId set to:', petData.value.userId);
});

const selectPet = (type: string) => {
  petData.value.type = type;
  console.log('Pet type selected:', type);
};

const handleInput = (event: Event) => {
  if ((event.target as HTMLInputElement).value) {
    document.querySelector('button')?.classList.add('glow-button');
  } else {
    document.querySelector('button')?.classList.remove('glow-button');
  }
};

const createPet = async (event: Event) => {
  console.log('CreatePet method called');
  event.preventDefault();
  if (petData.value.name && petData.value.type && petData.value.userId) {
    loading.value = true;
    message.value = '';
    try {
      console.log('Sending data to backend:', petData.value);
      const response = await axios.post(`${import.meta.env.VITE_BACKEND_BASE_URL || 'http://localhost:8080'}/api/create`, petData.value);
      if (response.status === 200) {
        const explosionElement = explosion.value;
        explosionElement.style.left = `${(event as MouseEvent).clientX - (explosionElement.clientWidth / 2)}px`;
        explosionElement.style.top = `${(event as MouseEvent).clientY - (explosionElement.clientHeight / 2)}px`;
        explosionElement.classList.add('explosion-active');

        setTimeout(() => {
          router.push({ name: 'pet', params: { petData: JSON.stringify(response.data) } });
          loading.value = false;
          message.value = 'Haustier erfolgreich erstellt!';
          messageType.value = 'success';
        }, 500);
      } else {
        console.error('Unexpected response status:', response.status);
      }
    } catch (error) {
      console.error('Fehler beim Erstellen des Haustiers:', error);
      message.value = 'Fehler beim Erstellen des Haustiers';
      messageType.value = 'error';
      loading.value = false;
    }
  } else {
    console.warn('Pet data is incomplete:', petData.value);
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
  position: relative;
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

.loader {
  border: 4px solid #f3f3f3;
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 12px;
  height: 12px;
  -webkit-animation: spin 1s linear infinite;
  animation: spin 1s linear infinite;
  display: inline-block;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  background: radial-gradient(
      circle,
      rgba(255, 255, 0, 1) 0%,
      rgba(255, 0, 0, 1) 50%,
      rgba(0, 0, 0, 0) 70%
  );
  opacity: 0;
  pointer-events: none;
}

.explosion-active {
  animation: explosion 0.5s ease-out forwards;
}

.message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 5px;
}

.message.success {
  background-color: #dff0d8;
  color: #3c763d;
}

.message.error {
  background-color: #f2dede;
  color: #a94442;
}
</style>
