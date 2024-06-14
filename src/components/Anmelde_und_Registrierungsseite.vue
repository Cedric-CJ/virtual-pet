<template>
  <div :class="['form-structor']">
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner"></div>
      <p v-if="isTimeout">Server ist gerade schwer zu erreichen, bitte warten Sie noch einen Moment.</p>
    </div>
    <div class="signup" :class="{ 'slide-up': isLogin }">
      <h2 class="form-title" @click="toggleForm">
        Registrieren
      </h2>
      <form @submit.prevent="handleRegister" class="form-holder">
        <input type="text" v-model="registerData.username" placeholder="Benutzername" class="input" required @keydown.enter="$event.target.nextElementSibling.focus()">
        <input type="password" v-model="registerData.password" placeholder="Passwort" class="input" required @keydown.enter="handleRegister">
      </form>
      <div class="animated-border">
        <button @click="handleRegister" class="submit-btn animated-text">Registrieren</button>
      </div>
      <p v-if="message && !isLogin" :class="{ error: isError, success: !isError }">{{ message }}</p>
    </div>
    <div class="login" :class="{ 'slide-up': !isLogin }">
      <div class="center">
        <h2 class="form-title" @click="toggleForm">
          Anmelden
        </h2>
        <form @submit.prevent="handleLogin" class="form-holder">
          <input type="text" v-model="loginData.username" placeholder="Benutzername" class="input" required @keydown.enter="$event.target.nextElementSibling.focus()">
          <input type="password" v-model="loginData.password" placeholder="Passwort" class="input" required @keydown.enter="handleLogin">
        </form>
        <div class="animated-border">
          <button @click="handleLogin" class="submit-btn animated-text">Anmelden</button>
        </div>
      </div>
      <p v-if="message && isLogin" :class="{ error: isError, success: !isError }">{{ message }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store'; // Verwenden Sie den richtigen Pfad

const isLogin = ref(true);
const loginData = ref({ username: '', password: '' });
const registerData = ref({ username: '', password: '' });
const message = ref('');
const isError = ref(false);
const isLoading = ref(false);
const isTimeout = ref(false);
const router = useRouter();
const store = useUserStore();

const handleRegister = async () => {
  isLoading.value = true;
  isTimeout.value = false;
  const timeout = setTimeout(() => {
    isTimeout.value = true;
  }, 5000);

  try {
    const response = await axios.post(`${import.meta.env.VITE_BACKEND_BASE_URL || 'http://localhost:8080'}/api/registration`, registerData.value);

    if (response.status === 200) {
      message.value = 'Registrierung erfolgreich!';
      isError.value = false;
      const userId = response.data.userId;
      store.updateUserId(userId);
    } else {
      throw new Error(response.data);
    }
  } catch (error) {
    message.value = 'Registrierung fehlgeschlagen: ' + (error.response?.data || 'Unbekannter Fehler');
    isError.value = true;
  } finally {
    clearTimeout(timeout);
    isLoading.value = false;
  }
};

const handleLogin = async () => {
  isLoading.value = true;
  isTimeout.value = false;
  const timeout = setTimeout(() => {
    isTimeout.value = true;
  }, 5000);

  try {
    const response = await axios.post("https://virtual-pet-backend.onrender.com/api/login", loginData.value);
    if (response.status === 200) {
      message.value = response.data.message;
      isError.value = false;
      localStorage.setItem('token', response.data.token);
      const userId = response.data.userId;
      store.updateUserId(userId); // Benutzer-ID im Pinia Store speichern
      isLoading.value = false;
      setTimeout(() => {
        if (response.data.hasPet) {
          router.push('/pet');
        } else {
          router.push('/create');
        }
      }, 2000); // Verzögerung vor dem Seitenwechsel
    } else {
      throw new Error(response.data);
    }
  } catch (error) {
    message.value = 'Login fehlgeschlagen: ' + (error.response?.data.message || 'Unbekannter Fehler');
    isError.value = true;
    isLoading.value = false;
  } finally {
    clearTimeout(timeout);
  }
};

const toggleForm = () => {
  isLogin.value = !isLogin.value;
};
</script>

<style scoped>
html, body {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "Fira Sans", Helvetica, Arial, sans-serif;
  overflow: hidden;
}

.form-structor {
  background-color: var(--background-color);
  border-radius: 15px;
  height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  text-align: center;
  justify-content: center;
  align-items: center;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #000;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.form-structor::before, .form-structor::after {
  content: "";
  position: absolute;
  top: 0;
  bottom: 0;
  width: 50%;
  background-size: cover;
  background-position: center;
  opacity: 0.5;
}

.form-structor::before {
  left: 0;
  background-image: url('@/assets/catfront.png');
}

.form-structor::after {
  right: 0;
  background-image: url('@/assets/dogfront.png');
}

.signup, .login {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 65%;
  z-index: 5;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.5); /* Light background for text readability */
  padding: 20px;
  border-radius: 15px;
}

.signup.slide-up, .login.slide-up {
  top: 5%;
  transform: translate(-50%, 0%);
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0); /* Light background for text readability */
}

.signup.slide-up .form-holder, .login.slide-up .form-holder {
  opacity: 0;
  visibility: hidden;
  background-color: rgba(255, 255, 255, 0)
}

.signup .submit-btn, .login .submit-btn {
  background-color: var(--button-background);
  color: var(--button-text);
  border: 0;
  border-radius: 15px;
  display: block;
  margin: 15px auto;
  padding: 15px 45px;
  width: 100%;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
  opacity: 1;
  visibility: visible;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.signup.slide-up .submit-btn, .login.slide-up .submit-btn {
  opacity: 0;
  visibility: hidden;
}

.signup .form-title, .login .form-title {
  color: var(--text-color);
  font-size: 1.7em;
  text-align: center;
  cursor: pointer;
}

.signup .input, .login .input {
  border: 0;
  outline: none;
  box-shadow: none;
  display: block;
  height: 30px;
  line-height: 30px;
  padding: 8px 15px;
  border-bottom: 1px solid #eee;
  width: 100%;
  font-size: 12px;
  background-color: var(--input-background);
  color: var(--input-text);
}

.signup .input:last-child, .login .input:last-child {
  border-bottom: 0;
}

.signup .input::-webkit-input-placeholder, .login .input::-webkit-input-placeholder {
  color: rgba(0, 0, 0, 1);
}

.theme-toggle-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  background-color: var(--button-background);
  color: var(--button-text);
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.animated-text {
  background: linear-gradient(45deg, #1598fc, #18ecfd, #26f0ba, #e6d05a, #fb9f56, #fa6984, #838ce1, #1598fc);
  background-size: 400%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: textAnimation 10s linear infinite;
}

.error {
  color: red;
}

.success {
  color: green;
}

@keyframes textAnimation {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 400% 0;
  }
}
</style>