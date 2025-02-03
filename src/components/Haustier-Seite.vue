<template>
  <div class="main-content">
    <div class="pet-info">
      <div class="pet-image-wrapper">
        <transition name="fade">
          <img v-if="currentImage" :src="currentImage" alt="Pet" class="pet-image" key="currentImage">
        </transition>
      </div>
      <h1>{{ store.petData.name }}</h1>
      <div class="actions" v-if="!isPetDead">
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Hunger < 20 }" @click= "performAction('feed')" :disabled="isActionInProgress">Füttern</button>
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Durst < 20 }" @click="performAction('water')" :disabled="isActionInProgress">Wasser geben</button>
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Energie < 20 }" @click="performAction('sleep')" :disabled="isActionInProgress">Schlafen</button>
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Komfort < 20 }" @click="performAction('pet')" :disabled="isActionInProgress">Streicheln</button>
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Komfort < 20 }" @click="performAction('clean')" :disabled="isActionInProgress">Duschen</button>
        <button v-if="store.petData.stats" :class="{ 'alert-button': store.petData.stats.Komfort < 20 }" @click="performAction('play')" :disabled="isActionInProgress">Spielen</button>
      </div>
      <transition name="fade">
        <p v-if="actionText" class="action-text">{{ actionText }}</p>
      </transition>
    </div>
    <div class="stats" v-if="!isPetDead">
      <div class="stat-bar" v-if="store.petData.stats" v-for="(value, key) in store.petData.stats" :key="key">
        <label>{{ key }}:</label>
        <div class="progress">
          <div class="progress-bar" :style="{ width: value + '%' }"></div>
        </div>
        <span>{{ value }}%</span>
      </div>
    </div>
    <div class="top-pets">
      <h3>Top 10 Pets</h3>
      <table>
        <thead>
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Days</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(pet, index) in sortedPets" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ pet.name }}</td>
          <td>{{ pet.daysAlive }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="isPetDead" class="modal">
      <div class="modal-content">
        <h2>Your {{ store.petData.name }} died. Do you want to create a new animal?</h2>
        <button @click="handleNewPet">Yes<br>(Create a new Pet)</button>
        <button @click="deleteAndLogout">No<br>(Delete Pet and Logout)</button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, nextTick, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store';
import { PetData } from '@/types';
const API_URL = 'https://virtual-pet-backend.onrender.com/api';
const pets = ref<any[]>([]);
const store = useUserStore();
const actionText = ref<string>('');
const currentImage = ref<string>('');
const router = useRouter();
const isPetDead = ref<boolean>(false);
const isActionInProgress = ref<boolean>(false);
const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});
const checkUserData = () => {
  store.loadUserData();
  if (!store.userId || !store.username) {
    console.error('UserID oder Benutzername fehlt');
    router.push('/login');
  } else {
    console.log('User ID:', store.userId);
    console.log('Username:', store.username);
  }
};
const getTopPets = async () => {
  try {
    const response = await axiosInstance.get('/top');
    pets.value = response.data.map((pet: any, index: number) => {
      const createdDate = new Date(pet.createdDate);
      const currentDate = new Date();
      const daysAlive = Math.floor((currentDate.getTime() - createdDate.getTime()) / (1000 * 60 * 60 * 24));
      return {
        name: pet.name,
        daysAlive: daysAlive,
        index: index + 1
      };
    });
  } catch (error) {
    handleError(error);
  }
};
const sortedPets = computed(() => {
  return pets.value.slice().sort((a, b) => b.daysAlive - a.daysAlive);
});
const getPetData = async () => {
  try {
    if (!store.userId || !store.username) {
      console.error('Keine Benutzer-ID oder Benutzername übergeben');
      return;
    }
    const response = await axiosInstance.post('/userpet', {
      userId: store.userId,
      username: store.username
    });
    if (response.data) {
      const petResponse = response.data;
      const petData: PetData = {
        name: petResponse.name,
        type: petResponse.type,
        stats: {
          Energie: petResponse.energie,
          Hunger: petResponse.hunger,
          Durst: petResponse.durst,
          Komfort: petResponse.komfort
        },
        createdDate: petResponse.createdDate,
        lastFed: petResponse.lastFed,
        lastWatered: petResponse.lastWatered,
        lastSlept: petResponse.lastSlept,
        lastPetted: petResponse.lastPetted,
        lastShowered: petResponse.lastShowered,
        username: petResponse.username,
        userId: petResponse.userId,
        dead: petResponse.dead
      };
      store.updatePetData(petData);
      checkIfPetIsDead();
      setInitialImage();
    } else {
      console.error('Unerwartete Datenstruktur erhalten:', response.data);
    }
  } catch (error) {
    handleError(error);
  }
};
const checkIfPetIsDead = () => {
  const { Energie, Hunger, Durst, Komfort } = store.petData.stats;
  if (Energie === 0 && Hunger === 0 && Durst === 0 && Komfort === 0) {
    isPetDead.value = true;
  }
};
const setInitialImage = () => {
  const { Energie, Hunger, Durst, Komfort } = store.petData.stats;
  let imageName;
  if (Energie < 30 || Hunger < 30 || Durst < 30 || Komfort < 30) {
    imageName = store.petData.type === 'dog' ? 'dognegativ.png' : 'catnegativ.png';
  } else if (Energie > 70 && Hunger > 70 && Durst > 70 && Komfort > 70) {
    imageName = store.petData.type === 'dog' ? 'dogpositiv.png' : 'catpositiv.png';
  } else {
    imageName = store.petData.type === 'dog' ? 'dogfront.png' : 'catfront.png';
  }

  currentImage.value = new URL(`../assets/pictures/${imageName}`, import.meta.url).href;
};
const handleNewPet = async () => {
  try {
    store.loadUserData();
    const userId = store.userId;
    const username = store.username;
    console.log('User ID nach Laden:', userId);
    console.log('Username nach Laden:', username);
    if (!userId || !username) {
      console.error('Keine Benutzer-ID oder Benutzername übergeben');
      return;
    }
    const payload = { userId, username };
    console.log('Sending delete request with:', payload);
    const deleteResponse = await axiosInstance.delete('/delete', { data: payload });
    if (deleteResponse.status === 200) {
      console.log('Antwort vom Backend:', deleteResponse.data);
      router.push('/create');
    } else {
      console.error('Fehler beim Löschen des Haustiers:', deleteResponse.data);
    }
  } catch (error) {
    handleError(error);
  }
};
const deleteAndLogout = async () => {
  try {
    const payload = { userId: store.userId, username: store.username };
    console.log('Sending delete request with:', payload);
    console.log('Authorization header:', axiosInstance.defaults.headers.Authorization);
    await axiosInstance.delete('/delete', { data: payload });
    router.push('/logout');
  } catch (error) {
    handleError(error);
  }
};
const reduceStatsOverTime = () => {
  setInterval(() => {
    store.petData.stats.Energie = Math.max(store.petData.stats.Energie - 1, 0);
    store.petData.stats.Hunger = Math.max(store.petData.stats.Hunger - 1, 0);
    store.petData.stats.Durst = Math.max(store.petData.stats.Durst - 1, 0);
    store.petData.stats.Komfort = Math.max(store.petData.stats.Komfort - 1, 0);
    checkIfPetIsDead();
  }, 3600000); // Alle 1 Stunde
};
onMounted(async () => {
  await checkUserData();
  if (store.userId && store.username) {
    await getPetData();
  } else {
    console.error('Keine Benutzer-ID oder Benutzername übergeben');
  }
  await getTopPets();
  reduceStatsOverTime();
});
const performAction = async (action: string) => {
  if (isActionInProgress.value) return;
  isActionInProgress.value = true;
  const now = new Date().toISOString();
  const actions: { [key: string]: () => void } = {
    feed: () => {
      const newHunger = Math.min(store.petData.stats.Hunger + 50, 100);
      store.updatePetStat('Hunger', newHunger);
      store.petData.lastFed = now;
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogessen.png', import.meta.url).href : new URL('../assets/pictures/catessen.png', import.meta.url).href);
    },
    water: () => {
      const newDurst = Math.min(store.petData.stats.Durst + 100, 100);
      store.updatePetStat('Durst', newDurst);
      store.petData.lastWatered = now;
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogtrinken.png', import.meta.url).href : new URL('../assets/pictures/cattrinken.png', import.meta.url).href);
    },
    sleep: () => {
      store.updatePetStat('Energie', 100);
      store.petData.lastSlept = now;
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogschlafen.png', import.meta.url).href : new URL('../assets/pictures/catschlafen.png', import.meta.url).href);
    },
    pet: () => {
      const newKomfort = Math.min(store.petData.stats.Komfort + 10, 100);
      store.updatePetStat('Komfort', newKomfort);
      store.petData.lastPetted = now;
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogstreicheln.png', import.meta.url).href : new URL('../assets/pictures/catstreicheln.png', import.meta.url).href);
    },
    clean: () => {
      const newKomfort = Math.min(store.petData.stats.Komfort + 100, 100);
      store.updatePetStat('Komfort', newKomfort);
      store.petData.lastShowered = now;
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogduschen.png', import.meta.url).href : new URL('../assets/pictures/catduschen.png', import.meta.url).href);
    },
    play: () => {
      const newEnergie = Math.max(store.petData.stats.Energie - 10, 0);
      const newKomfort = Math.min(store.petData.stats.Komfort + 10, 100);
      store.updatePetStat('Energie', newEnergie);
      store.updatePetStat('Komfort', newKomfort);
      changeImage(store.petData.type === 'dog' ? new URL('../assets/pictures/dogspielen.png', import.meta.url).href : new URL('../assets/pictures/catspielen.png', import.meta.url).href);
    }
  };
  actions[action]();
  console.log('Aktualisierte Haustierdaten vor dem Senden an das Backend:', store.petData);
  updateActionText(action);
  await nextTick();
  const statsToSend = {
    hunger: store.petData.stats.Hunger,
    durst: store.petData.stats.Durst,
    energie: store.petData.stats.Energie,
    komfort: store.petData.stats.Komfort
  };
  const lastActionDatesToSend = {
    lastFed: store.petData.lastFed,
    lastWatered: store.petData.lastWatered,
    lastSlept: store.petData.lastSlept,
    lastPetted: store.petData.lastPetted,
    lastShowered: store.petData.lastShowered
  };
  try {
    const responseStats = await axiosInstance.post('/updateStats', { stats: statsToSend, userId: store.petData.userId, name: store.petData.name });
    console.log('Antwort vom Backend (Statistiken):', responseStats.data);
    const responseLastActions = await axiosInstance.post('/updateLastActions', { lastActions: lastActionDatesToSend, userId: store.petData.userId, name: store.petData.name });
    console.log('Antwort vom Backend (letzte Aktionen):', responseLastActions.data);
    checkIfPetIsDead();
    setTimeout(() => {
      setInitialImage();
    }, 2000);
  } catch (error) {
    handleError(error);
  } finally {
    isActionInProgress.value = false;
  }
};
const changeImage = (newImage: string) => {
  currentImage.value = newImage;
  setTimeout(() => {
    isActionInProgress.value = false;
    setInitialImage();
  }, 3000);
};
const updateActionText = (action: string) => {
  const actionMessages: { [key: string]: string } = {
    feed: 'Hunger +50',
    water: 'Durst +100',
    sleep: 'Energie aufgefüllt',
    pet: 'Komfort +10',
    clean: 'Komfort +100',
    play: 'Energie -10, Komfort +10'
  };
  actionText.value = actionMessages[action];
  setTimeout(() => {
    actionText.value = '';
  }, 3000);
};
const handleError = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    console.error('Error response:', error.response ? error.response.data : error.message);
  } else {
    console.error('Unexpected error:', error);
  }
};
</script>
<style scoped>
body {
  font-family: 'Arial', sans-serif;
  transition: background-color 0.3s, color 0.3s;
}

.main-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  min-height: 100vh;
  max-width: 100vw;
  overflow-x: auto;
  position: relative;
}

@media (min-width: 1024px) {
  .main-content {
    flex-direction: row;
    align-items: flex-start;
    justify-content: space-around;
  }

  .pet-info {
    flex: 1;
    text-align: center;
    margin-right: 20px;
  }

  .stats, .top-pets {
    flex: 1;
    order: 2;
  }
}

.pet-image-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 300px;
}

.pet-image {
  max-width: 150%;
  max-height: 150%;
  display: block;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 100px;
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
  z-index: 10;
}

.stats {
  position: absolute;
  top: 20px;
  left: 20px;
  min-width: 200px;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

body.dark-mode .stats {
  background-color: #333;
  color: #fff;
}

.stat-bar {
  width: 100%;
  margin: 10px 0;
}

.progress {
  background-color: #e0e0e0;
  width: 100%;
  height: 20px;
  border-radius: 10px;
  position: relative;
}

.progress-bar {
  background-color: #4CAF50;
  height: 100%;
  transition: width 0.3s ease;
}

.progress span {
  position: absolute;
  right: 5px;
  top: 1px;
  color: black;
  font-size: 14px;
}

.top-pets {
  position: absolute;
  top: 160px;
  right: 20px;
  min-width: 300px;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

body.dark-mode .top-pets {
  background-color: #333;
  color: #fff;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #333;
  color: #fff;
}

.alert-button {
  background-color: red;
  color: white;
}

@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .stats, .top-pets {
    padding: 10px;
    min-width: 100%;
    position: static;
    margin: 10px 0;
  }

  .actions {
    flex-direction: column;
  }

  .action-text {
    bottom: 10px;
    left: 10px;
    transform: translateX(0);
  }
}

/* Modal Styles */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

body.dark-mode .modal-content {
  background: #333;
  color: #fff;
}

.modal-content button {
  margin: 10px;
}

/* Dark Mode Styles for Other Elements */
body.dark-mode .stats,
body.dark-mode .top-pets {
  background-color: #333;
  color: #fff;
}

body.dark-mode .action-text {
  background-color: #000;
  color: #fff;
}
</style>