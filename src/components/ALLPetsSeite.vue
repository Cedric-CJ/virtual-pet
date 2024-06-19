<template>
  <div class="dead-pets">
    <h1>Tierliste</h1>
    <table>
      <thead>
      <tr>
        <th @click="sortTable('index')">#</th>
        <th @click="sortTable('name')">Name</th>
        <th @click="sortTable('type')">Typ</th>
        <th @click="sortTable('username')">Benutzername</th>
        <th @click="sortTable('createdDate')">Erstellungsdatum</th>
        <th @click="sortTable('lastFed')">Letzte Fütterung</th>
        <th @click="sortTable('lastWatered')">Letzte Bewässerung</th>
        <th @click="sortTable('lastSlept')">Letztes Schlafen</th>
        <th @click="sortTable('lastPetted')">Letztes Streicheln</th>
        <th @click="sortTable('lastShowered')">Letzte Dusche</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(pet, index) in sortedPets" :key="index">
        <td>{{ pet.dead ? '†' : '' }}{{ index + 1 }}</td>
        <td>{{ pet.name }}</td>
        <td>{{ pet.type }}</td>
        <td>{{ pet.username }}</td>
        <td>{{ formatDate(pet.createdDate) }}</td>
        <td>{{ formatTimeAgo(pet.lastFed) }}</td>
        <td>{{ formatTimeAgo(pet.lastWatered) }}</td>
        <td>{{ formatTimeAgo(pet.lastSlept) }}</td>
        <td>{{ formatTimeAgo(pet.lastPetted) }}</td>
        <td>{{ formatTimeAgo(pet.lastShowered) }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { formatDistanceToNow, parseISO } from 'date-fns';
import { de } from 'date-fns/locale';

const deadPets = ref<any[]>([]);
const sortedPets = ref<any[]>([]);
const sortKey = ref<string>('');
const sortOrder = ref<string>('asc');
const API_URL = 'https://virtual-pet-backend.onrender.com/api';

const getDeadPets = async () => {
  try {
    const response = await axios.get(`${API_URL}/allPets`);
    deadPets.value = response.data.map((pet: any, index: number) => ({
      ...pet,
      index: index + 1
    }));
    sortTable('index');
  } catch (error) {
    console.error('Fehler beim Laden der Tiere:', error);
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('de-DE');
};

const formatTimeAgo = (dateString: string | null) => {
  if (!dateString) return 'Nie';
  return formatDistanceToNow(parseISO(dateString), { addSuffix: true, locale: de });
};

const sortTable = (key: string) => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortKey.value = key;
    sortOrder.value = 'asc';
  }
  sortedPets.value = [...deadPets.value].sort((a, b) => {
    if (a[key] < b[key]) return sortOrder.value === 'asc' ? -1 : 1;
    if (a[key] > b[key]) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
};

onMounted(() => {
  getDeadPets();
});
</script>

<style scoped>
.dead-pets {
  padding: 20px;
  padding: 50px 20px 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
  cursor: pointer;
}

th {
  background-color: #333;
  color: #fff;
}
</style>