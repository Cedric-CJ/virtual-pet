<template>
  <div class="navbar">
    <div class="logo" @click="toggleDropdown">
      <img src="@/assets/Logo.png" alt="Virtuelles Haustier Logo">
    </div>
    <transition name="fade">
      <div class="dropdown" v-if="dropdownVisible">
      <h1>Herzlich Willkommen</h1>
      <nav>
        <RouterLink to="/login">Login</RouterLink>
        <RouterLink to="/pet">Pet</RouterLink>
        <RouterLink to="/logout">Abmelden</RouterLink>
      </nav>
      <a href="#" @click="toggleDarkMode">Dark Mode</a>
    </div>
    </transition>
  </div>
  <RouterView/>
</template>

<script setup>
import { ref } from 'vue';

const dropdownVisible = ref(false);

const toggleDropdown = () => {
  dropdownVisible.value = !dropdownVisible.value;
};

const toggleDarkMode = () => {
  const currentMode = document.body.classList.contains('dark-mode') ? 'dark-mode' : 'light-mode';
  const newMode = currentMode === 'dark-mode' ? 'light-mode' : 'dark-mode';
  document.body.classList.remove(currentMode);
  document.body.classList.add(newMode);
  dropdownVisible.value = false; // Dropdown schließen, nachdem der Modus umgeschaltet wurde
};
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 1000;
  display: flex;
  align-items: center;
}

.logo {
  cursor: pointer;
  background-color: var(--logo-bg, #ffffff);
}

.logo img {
  max-width: 150px;
}

.dropdown {
  position: absolute;
  top: 150px;
  right: 0;
  background-color: var(--dropdown-bg, #ffffff);
  border: 1px solid #ccc;
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  z-index: 1000;
}

.dropdown h1 {
  margin: 10px 0;
}

nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

nav a {
  text-decoration: none;
  color: var(--link-color, black);
  padding: 10px;
  width: 100%;
  text-align: center;
}

nav a:hover {
  background-color: var(--link-hover-bg, #ddd);
}

.dropdown a {
  text-decoration: none;
  color: var(--link-color, black);
  padding: 10px;
  width: 100%;
  text-align: center;
}

.dropdown a:hover {
  background-color: var(--link-hover-bg, #ddd);
}

/* Dark Mode Variables */
body.dark-mode {
  --dropdown-bg: #333;
  --link-color: #fff;
  --link-hover-bg: #555;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>