<template>
  <div class="register-container">
    <h1>Registrieren</h1>
    <form @submit.prevent="handleRegister">
      <input type="text" v-model="registerData.username" placeholder="Benutzername" required>
      <input type="password" v-model="registerData.password" placeholder="Passwort" required>
      <button type="submit">Registrieren</button>
    </form>
    <p v-if="message" :class="{ error: isError, success: !isError }">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const registerData = ref({
  username: '',
  password: ''
});
const message = ref('');
const isError = ref(false);
const router = useRouter();

const handleRegister = async () => {
  try {
    const response = await axios.post('/api/registration', registerData.value);
    console.log('Registrierungsantwort:', response.data);
    if (response.status === 200) {
      router.push('/login'); // Bei Erfolg weiterleiten
    } else {
      throw new Error('Registrierung fehlgeschlagen!');
    }
  } catch (error) {
    console.error('Fehler bei der Registrierung:', error);
  }
};
</script>

<style scoped>
.register-container {
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