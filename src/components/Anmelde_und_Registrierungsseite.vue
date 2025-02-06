<template>
  <div class="rotating-bg"></div>
  <div :class="['form-structor']">
    <div v-if="isLoading" class="loading-overlay">
      <div class="spinner"></div>
      <p v-if="isTimeout" class="timeout-text">
        Backend server is starting. Please be patient for a moment, as this is a free server it may take up to a minute.
      </p>
    </div>
    <div class="signup" :class="{ 'slide-up': isLogin }">
      <h2 class="form-title responsive-text" @click="toggleForm">
        Register here
      </h2>
      <form @submit.prevent="handleRegister" class="form-holder">
        <input type="text" v-model="registerData.username" placeholder="Username" class="input" required @keydown.enter="focusNextInput">
        <input type="password" v-model="registerData.password" placeholder="Password" class="input" required @keydown.enter="handleRegister">
      </form>
      <div class="animated-border">
        <button @click="handleRegister" class="submit-btn animated-text">Register</button>
      </div>
      <p v-if="message && !isLogin" :class="{ error: isError, success: !isError }">{{ message }}</p>
    </div>
    <div class="login" :class="{ 'slide-up': !isLogin }">
      <div class="center">
        <h2 class="form-title responsive-text" @click="toggleForm">
          Login here
        </h2>
        <form @submit.prevent="handleLogin" class="form-holder">
          <input type="text" v-model="loginData.username" placeholder="Username" class="input" required @keydown.enter="focusNextInput">
          <input type="password" v-model="loginData.password" placeholder="Password" class="input" required @keydown.enter="handleLogin">
        </form>
        <div class="animated-border">
          <button @click="handleLogin" class="submit-btn animated-text">Login</button>
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
import { useUserStore } from '@/store';

const isLogin = ref(true);
const loginData = ref({ username: '', password: '' });
const registerData = ref({ username: '', password: '' });
const message = ref('');
const isError = ref(false);
const isLoading = ref(false);
const isTimeout = ref(false);
const router = useRouter();
const store = useUserStore();

const focusNextInput = (event: KeyboardEvent) => {
  const target = event.target as HTMLInputElement;
  const nextSibling = target.nextElementSibling as HTMLInputElement | null;
  if (nextSibling) {
    nextSibling.focus();
  }
};
const handleRegister = async () => {
  isLoading.value = true;
  isTimeout.value = false;
  const timeout = setTimeout(() => {
    isTimeout.value = true;
  }, 5000);
  try {
    const response = await axios.post("https://virtual-pet-backend.onrender.com/api/registration", registerData.value);
    if (response.status === 200) {
      message.value = 'Registration successful!';
      isError.value = false;
    } else {
      throw new Error(response.data);
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      message.value = 'Registration failed: ' + (error.response?.data || 'Unknown error');
    } else {
      message.value = 'Registration failed: Unknown error';
    }
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
      message.value = 'Login successful!';
      isError.value = false;
      localStorage.setItem('token', response.data.token);
      const userId = response.data.userId;
      const username = response.data.username;
      store.updateUserId(userId);
      store.updateUsername(username);
      isLoading.value = false;
      setTimeout(() => {
        if (response.data.hasPet) {
          router.push({ name: 'Pet'});
        } else {
          router.push('/create');
        }
      }, 2000);
    } else {
      throw new Error(response.data);
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const errorMessage = error.response?.data.message || error.response?.data || 'Unknown error';
      message.value = 'Login failed: ' + errorMessage;
    } else {
      message.value = 'Login failed: Unknown error';
    }
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
.rotating-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('@/assets/pictures/Mittelalter.png');
  background-size: cover;
  background-repeat: repeat-x;
  animation: rotateBg 30s linear infinite;
  z-index: -1;
}
.form-structor {
  border-radius: 15px;
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  text-align: center;
  justify-content: center;
  align-items: center;
  z-index: 1;
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
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  z-index: 1;
}
.form-structor::before {
  left: 0;
  background-image: url('@/assets/pictures/catfront.png');
  background-size: 90%;
}
.form-structor::after {
  right: 0;
  background-image: url('@/assets/pictures/dogfront.png');
  background-size: 90%;
}
.signup, .login {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: auto;
  min-width: 250px;
  z-index: 5;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 15px;
}
.signup.slide-up, .login.slide-up {
  top: 75%;
  transform: translate(-50%, 0%);
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.8);
  width: auto;
  height: 5%;
  text-align: center;
  padding: 5px;
  line-height: 0;
}
.signup.slide-up .form-holder, .login.slide-up .form-holder {
  opacity: 0;
  visibility: hidden;
  background-color: transparent;
}
.signup .submit-btn, .login .submit-btn {
  border: 0;
  border-radius: 15px;
  display: block;
  margin: 15px auto;
  padding: 15px 45px;
  width: 100%;
  font-size: 1rem;
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
  white-space: nowrap;
}
.signup .input, .login .input {
  border: 0;
  outline: none;
  box-shadow: none;
  display: block;
  height: 30px;
  line-height: 30px;
  padding: 5px 0;
  border-bottom: 1px solid #eee;
  width: 100%;
  font-size: 12px;
  background-color: transparent;
}
.signup .input:last-child, .login .input:last-child {
  border-bottom: 0;
}
.signup .input::-webkit-input-placeholder, .login .input::-webkit-input-placeholder {
  color: rgba(0, 0, 0, 1);
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
@keyframes rotateBg {
  from {
    background-position: 0 0;
  }
  to {
    background-position: -1000px 0;
  }
}
</style>