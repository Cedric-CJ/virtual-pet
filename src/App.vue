<template>
  <header :class="{ 'collapsed': !isExpanded, 'expanded': isExpanded }">
    <div class="header-content" @mouseover="handleMouseOver" @mouseleave="handleMouseLeave">
      <div class="logo">
        <img src="@/assets/Logo.png" alt="Virtuelles Haustier Logo">
      </div>
      <h1>Herzlich Willkommen bei deinem Virtuellen Haustier</h1>
      <nav>
        <RouterLink to="/login">Login</RouterLink>
        <RouterLink to="/pet">Pet</RouterLink>
        <RouterLink to="/logout">Abmelden</RouterLink>
      </nav>
    </div>
    <div class="toggle-arrow" @click="toggleExpansion"></div>
  </header>
  <RouterView/>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const isExpanded = ref(true);

watch(() => route.path, (newPath) => {
  if (['/registration', '/login', '/logout'].includes(newPath)) {
    isExpanded.value = true;
  } else {
    isExpanded.value = false; // Für alle anderen Routen soll die Kopfzeile eingeklappt bleiben
  }
});

const handleMouseOver = () => {
  if (!['/registration', '/login', '/logout'].includes(route.path)) {
    isExpanded.value = true;
  }
};

const handleMouseLeave = () => {
  if (!['/registration', '/login', '/logout'].includes(route.path)) {
    isExpanded.value = false;
  }
};

function toggleExpansion() {
  isExpanded.value = !isExpanded.value;
}
</script>

<style scoped>
header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: #0a0a0a;
  color: white;
  transition: max-height 0.5s ease;
  overflow: hidden;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.collapsed {
  max-height: 10px;
}

.expanded {
  max-height: 100vh;
}

.header-content {
  width: 100%;
  padding: 20px;
  text-align: center;
}

.logo img {
  max-width: 100px;
  margin-bottom: 10px;
}

nav {
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

nav a {
  text-decoration: none;
  color: white;
  padding: 10px;
}

nav a:hover {
  background-color: #555;
}

.toggle-arrow {
  position: absolute;
  top: 5px;
  right: 5px;
  cursor: pointer;
  font-size: 24px;
}
</style>