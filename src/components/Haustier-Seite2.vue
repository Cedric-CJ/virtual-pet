<template>
  <div class="main-content">
    <div class="pet-info">
      <transition name="fade">
        <img v-if="currentImage" :src="currentImage" alt="Pet" class="pet-image" key="currentImage">
      </transition>
      <h1>{{ petData.name }}</h1>
      <div class="actions">
        <button @click="performAction('feed')">Füttern</button>
        <button @click="performAction('water')">Wasser geben</button>
        <button @click="performAction('sleep')">Schlafen</button>
        <button @click="performAction('pet')">Streicheln</button>
        <button @click="performAction('clean')">Duschen</button>
        <button @click="performAction('play')">Spielen</button>
        <button @click="saveAndLogout">Speichern und Abmelden</button>
      </div>
      <transition name="fade">
        <p v-if="actionText" class="action-text">{{ actionText }}</p>
      </transition>
    </div>
    <div class="stats">
      <div class="stat-bar" v-for="(value, key) in petData.stats" :key="key">
        <label>{{ key }}:</label>
        <div class="progress">
          <div class="progress-bar" :style="{width: value + '%' }"></div>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const API_URL = 'https://virtual-pet-backend.onrender.com/api';
const pets = ref([]);
const petData = ref({
  name: '',
  type: '',
  stats: {
    energie: 80,
    hunger: 20,
    durst: 35,
    komfort: 75
  }
});
const actionText = ref('');
const currentImage = ref('');
const route = useRoute();
const router = useRouter();

const getTopPets = async () => {
  try {
    const response = await axios.get(`${API_URL}/pet/top`);
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
    const response = await axios.get(`${API_URL}/pet/${route.params.petId}`);
    petData.value = response.data;
  } catch (error) {
    console.error('Error fetching pet data:', error.response ? error.response.data : error);
  }
};

const saveAndLogout = async () => {
  try {
    const response = await axios.post(`${API_URL}/pet/save`, petData.value);
    console.log('Pet data saved:', response.data);
    router.push('/login');
  } catch (error) {
    console.error('Error saving pet data:', error.response ? error.response.data : error);
  }
};

onMounted(() => {
  getPetData();
  getTopPets();
});

const performAction = (action) => {
  const actions = {
    feed: () => {
      petData.value.stats.hunger = Math.min(petData.value.stats.hunger + 50, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogessen.png' : 'src/assets/catessen.png');
    },
    water: () => {
      petData.value.stats.durst = Math.min(petData.value.stats.durst + 100, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogtrinken.png' : 'src/assets/cattrinken.png');
    },
    sleep: () => {
      petData.value.stats.energie = 100;
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogschlafen.png' : 'src/assets/catschlafen.png');
    },
    pet: () => {
      petData.value.stats.komfort = Math.min(petData.value.stats.komfort + 10, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogstreicheln.png' : 'src/assets/catstreicheln.png');
    },
    clean: () => {
      petData.value.stats.komfort = Math.min(petData.value.stats.komfort + 100, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogduschen.png' : 'src/assets/catduschen.png');
    },
    play: () => {
      petData.value.stats.energie = Math.max(petData.value.stats.energie - 10, 0);
      petData.value.stats.komfort = Math.min(petData.value.stats.komfort + 10, 100);
      changeImage(petData.value.type === 'dog' ? 'src/assets/dogspielen.png' : 'src/assets/catspielen.png');
    }
  };
  actions[action]();
  updateActionText(action);
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
.main-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 25px;
  min-height: 100vh;
  max-width: 100vw;
  overflow-x: auto;
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

.pet-image {
  max-width: 50%;
  height: auto;
  display: block;
  margin: 0 auto;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
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
  flex: 1;
  min-width: 300px;
  padding: 20px;
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
  flex: 1;
  min-width: 200px;
  padding: 20px;
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

@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .stats, .top-pets {
    padding: 10px;
    min-width: 100%;
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
</style>