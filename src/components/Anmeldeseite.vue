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
    if (response.status === 200) {
      message.value = response.data.includes('hat bereits ein Haustier') ? 'Anmeldung erfolgreich! Sie haben bereits ein Haustier.' : 'Anmeldung erfolgreich!';
      isError.value = false;
      router.push('/pet'); // Bei Erfolg weiterleiten
    } else {
      throw new Error(response.data);
    }
  } catch (error) {
    message.value = 'Login fehlgeschlagen: ' + (error.response?.data.message || 'Unbekannter Fehler');
    isError.value = true;
  }
};

</script>

<style scoped>
.login-container {
  max-width: 300px;
  margin: 300px auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  background: Black;
  border-radius: 8px;
}

input {
  display: block;
  width: 100%;
  margin: 10px 0;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  margin-top: 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

button:hover {
  background-color: #45a049;
}

.error {
  color: red;
}

.success {
  color: green;
}

router-link {
  display: block;
  margin-top: 10px;
  color: #0645ad;
  text-decoration: none;
}

router-link:hover {
  text-decoration: underline;
}
</style>
