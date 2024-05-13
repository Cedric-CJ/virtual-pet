<template>
  <div class="logout-container">
    <h1>Abmelden</h1>
    <p>Sind Sie sicher, dass Sie sich abmelden möchten?</p>
    <button @click="handleLogout">Abmelden</button>
    <router-link to="/login">Zurück zum Login</router-link>
    <p v-if="message" :class="{ error: isError, success: !isError }">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const message = ref('');
const isError = ref(false);
const router = useRouter();

const handleLogout = async () => {
  try {
    // Hier würde der Logout-Aufruf an das Backend gehen, falls nötig
    await axios.post('/api/logout');
    message.value = 'Sie wurden erfolgreich abgemeldet!';
    isError.value = false;
    // Leite den Benutzer zur Login-Seite oder Startseite um
    setTimeout(() => {
      router.push('/login');
    }, 2000); // gibt dem Benutzer Zeit, die Nachricht zu lesen
  } catch (error) {
    console.error('Fehler beim Abmelden:', error);
    message.value = 'Fehler beim Abmelden.';
    isError.value = true;
  }
};
</script>

<style scoped>
.logout-container {
  max-width: 300px;
  margin: auto;
  padding: 20px;
  text-align: center;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

button {
  margin: 20px 0;
  padding: 10px 20px;
  cursor: pointer;
}

.error {
  color: red;
}

.success {
  color: green;
}
</style>
