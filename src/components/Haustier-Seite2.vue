<template>
  <div class="main-content">
    <div class="pet-info">
      <div class="pet-image-wrapper">
        <transition name="fade">
          <img v-if="currentImage" :src="currentImage" alt="Pet" class="pet-image" key="currentImage">
        </transition>
      </div>
      <h1>{{ petData.name }}</h1>
      <div class="actions" v-if="!isPetDead">
        <button v-if="petData.stats" :class="{ 'alert-button': petData.stats.Hunger < 20 }" @click="performAction('feed')">Füttern</button>
        <button v-if="petData.stats" :class="{ 'alert-button': petData.stats.Durst < 20 }" @click="performAction('water')">Wasser geben</button>
        <button v-if="petData.stats" :class="{ 'alert-button': petData.stats.Energie < 20 }" @click="performAction('sleep')">Schlafen</button>
        <button v-if="petData.stats" :class="{ 'alert-button': petData.stats.Komfort < 20 }" @click="performAction('pet')">Streicheln</button>
        <button v-if="petData.stats" @click="performAction('clean')">Duschen</button>
        <button v-if="petData.stats" @click="performAction('play')">Spielen</button>
      </div>
      <transition name="fade">
        <p v-if="actionText" class="action-text">{{ actionText }}</p>
      </transition>
    </div>
    <div class="stats" v-if="!isPetDead">
      <div class="stat-bar" v-if="petData.stats" v-for="(value, key) in petData.stats" :key="key">
        <label>{{ key }}:</label>
        <div class="progress">
          <div class="progress-bar" :style="{ width: value + '%' }"></div>
        </div>
        <span>{{ value }}%</span>
      </div>
    </div>
    <div class="top-pets">
      <h3>Top 10 Tiere</h3>
      <table>
        <thead>
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Tage</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(pet, index) in pets" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ pet.name }}</td>
          <td>{{ pet.daysAlive }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="isPetDead" class="modal">
      <div class="modal-content">
        <h2>Ihr {{ petData.name }} ist gestorben. Wollen Sie ein neues Tier erstellen?</h2>
        <button @click="handleNewPet">Ja</button>
        <button @click="handleLogout">Nein</button>
      </div>
    </div>
    <div v-if="showConfirmDelete" class="modal">
      <div class="modal-content">
        <h2>Wollen Sie Ihr Tier löschen?</h2>
        <button @click="deleteAndLogout">Ja</button>
        <button @click="logout">Nein</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import axios from 'axios';
import {useRoute, useRouter} from 'vue-router';
import {useUserStore} from '@/store';

const API_URL = 'https://virtual-pet-backend.onrender.com/api';
const pets = ref([]);
const petData = ref({
  name: '',
  type: '',
  stats: {
    Energie: 0,
    Hunger: 0,
    Durst: 0,
    Komfort: 0
  },
  createdDate: '',
  lastFed: '',
  lastWatered: '',
  lastSlept: '',
  lastPetted: '',
  lastShowered: '',
  username: ''
});
const actionText = ref('');
const currentImage = ref('');
const route = useRoute();
const router = useRouter();
const store = useUserStore();
const isPetDead = ref(false);
const showConfirmDelete = ref(false);

const token = localStorage.getItem('token');

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    'Authorization': `Bearer ${token}`
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
    pets.value = response.data.map((pet, index) => ({
      name: pet.name,
      daysAlive: pet.daysAlive,
      index: index + 1
    }));
  } catch (error) {
    console.error('Error fetching top pets:', error.response ? error.response.data : error);
  }
};

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
      petData.value.name = petResponse.name;
      petData.value.type = petResponse.type;
      petData.value.stats.Energie = petResponse.energie;
      petData.value.stats.Hunger = petResponse.hunger;
      petData.value.stats.Durst = petResponse.durst;
      petData.value.stats.Komfort = petResponse.komfort;
      petData.value.createdDate = petResponse.createdDate;
      petData.value.lastFed = petResponse.lastFed;
      petData.value.lastWatered = petResponse.lastWatered;
      petData.value.lastSlept = petResponse.lastSlept;
      petData.value.lastPetted = petResponse.lastPetted;
      petData.value.lastShowered = petResponse.lastShowered;
      petData.value.username = petResponse.username;

      checkIfPetIsDead();
    } else {
      console.error('Unerwartete Datenstruktur erhalten:', response.data);
    }
  } catch (error) {
    console.error('Fehler beim Abrufen der Tierdaten:', error.response ? error.response.data : error);
  }
};

const checkIfPetIsDead = () => {
  const {Energie, Hunger, Durst, Komfort} = petData.value.stats;
  if (Energie === 0 && Hunger === 0 && Durst === 0 && Komfort === 0) {
    isPetDead.value = true;
  }
};

const handleNewPet = async () => {
  try {
    await axiosInstance.delete('/delete', {data: {userId: store.userId, userName: store.username}});
    router.push('/create');
  } catch (error) {
    console.error('Fehler beim Löschen des Tieres:', error.response ? error.response.data : error);
  }
};

const handleLogout = () => {
  showConfirmDelete.value = true;
};

const deleteAndLogout = async () => {
  try {
    await axiosInstance.delete('/delete', {data: {userId: store.userId, userName: store.username}});
    router.push('/logout');
  } catch (error) {
    console.error('Fehler beim Löschen des Tieres:', error.response ? error.response.data : error);
  }
};

const logout = () => {
  router.push('/logout');
};

const reduceStatsOverTime = () => {
  setInterval(() => {
    petData.value.stats.Energie = Math.max(petData.value.stats.Energie - 5, 0);
    petData.value.stats.Hunger = Math.max(petData.value.stats.Hunger - 5, 0);
    petData.value.stats.Durst = Math.max(petData.value.stats.Durst - 5, 0);
    petData.value.stats.Komfort = Math.max(petData.value.stats.Komfort - 5, 0);
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

const performAction = async (action) => {
  const now = new Date().toISOString();
  const actions = {
    feed: () => {
      petData.value.stats.Hunger = Math.min(petData.value.stats.Hunger + 50, 100);
      petData.value.lastFed = now;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogessen.png' : 'src/assets/catessen.png');
    },
    water: () => {
      petData.value.stats.Durst = Math.min(petData.value.stats.Durst + 100, 100);
      petData.value.lastWatered = now;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogtrinken.png' : 'src/assets/cattrinken.png');
    },
    sleep: () => {
      petData.value.stats.Energie = 100;
      petData.value.lastSlept = now;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogschlafen.png' : 'src/assets/catschlafen.png');
    },
    pet: () => {
      petData.value.stats.Komfort = Math.min(petData.value.stats.Komfort + 10, 100);
      petData.value.lastPetted = now;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogstreicheln.png' : 'src/assets/catstreicheln.png');
    },
    clean: () => {
      petData.value.stats.Komfort = Math.min(petData.value.stats.Komfort + 100, 100);
      petData.value.lastShowered = now;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogduschen.png' : 'src/assets/catduschen.png');
    },
    play: () => {
      petData.value.stats.Energie = Math.max(petData.value.stats.Energie - 10, 0);
      petData.value.stats.Komfort = Math.min(petData.value.stats.Komfort + 10, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogspielen.png' : 'src/assets/catspielen.png');
    }
  };
  actions[action]();
  updateActionText(action);

  try {
    await axiosInstance.post('/save', petData.value);
    checkIfPetIsDead();
  } catch (error) {
    console.error('Fehler beim Speichern der Tierdaten:', error.response ? error.response.data : error);
  }
};

const changeImage = (newImage) => {
  const frontImage = petData.value.type === 'dog' ? 'src/assets/dogfront.png' : 'src/assets/catfront.png';
  currentImage.value = newImage;
  setTimeout(() => {
    currentImage.value = frontImage;
  }, 5000);
};

const updateActionText = (action) => {
  const actionMessages = {
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

.modal-content button {
  margin: 10px;
}
</style>