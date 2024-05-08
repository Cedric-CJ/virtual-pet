<template>
  <div class="login-container">
    <h1>Anmelden</h1>
    <form @submit.prevent="handleLogin">
      <input type="text" v-model="loginData.username" placeholder="Benutzername" required>
      <input type="password" v-model="loginData.password" placeholder="Passwort" required>
      <button type="submit">Anmelden</button>
    </form>
    <router-link to="/registration">Hier registrieren!</router-link>
    <p v-if="message" :class="{ error: isError, success: !isError }">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const loginData = ref({
  username: '',
  password: ''
});
const message = ref('');
const isError = ref(false);
const router = useRouter();

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/login', loginData.value);
    console.log('Login-Versuch für:', loginData.value.username);
    if (response.status === 200) {
      message.value = 'Login erfolgreich!';
      isError.value = false;
      router.push('/pet'); // Erfolgreich eingeloggt, weiterleiten zu '/pet'
    } else {
      throw new Error('Anmeldung fehlgeschlagen!');
    }
  } catch (error) {
    console.error('Login Fehler:', error);
    message.value = 'Login fehlgeschlagen: ' + (error.response.data.message || 'Unbekannter Fehler');
    isError.value = true;
  }
};
</script>

<style scoped>
.login-container {
  max-width: 300px;
  margin: auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.error {
  color: red;
}

.success {
  color: green;
}
</style>
