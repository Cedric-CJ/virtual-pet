<template>
  <div class="navbar">
    <div class="logo" @click.stop="toggleDropdown">
      <img src="@/assets/Logo.png" alt="Virtuell Pet Logo" />
    </div>
    <transition name="roll">
      <div class="dropdown" v-if="dropdownVisible">
        <h1>Virtual Pet</h1>
        <nav>
          <button
              v-if="!isUserLoggedIn"
              class="dropdown-button"
              @click="delayedNavigate('/login')"
              @mouseenter="showUnderline($event)"
              @mouseleave="hideUnderline"
          >
            Login
          </button>
          <button
              v-else
              class="dropdown-button"
              @click="delayedLogout"
              @mouseenter="showUnderline($event)"
              @mouseleave="hideUnderline"
          >
            Logout
          </button>
          <button
              class="dropdown-button"
              @click="delayedNavigate('/All')"
              @mouseenter="showUnderline($event)"
              @mouseleave="hideUnderline"
          >
            Topscore
          </button>
          <button
              @mouseenter="showUnderline($event)"
              @mouseleave="hideUnderline"
              @click="toggleDarkMode"
              class="dark-mode-link"
          >
            {{ darkModeText }}
          </button>
        </nav>
      </div>
    </transition>
  </div>
  <RouterView />
</template><script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { annotate } from "https://unpkg.com/rough-notation?module";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store";

const store = useUserStore();
const darkModeText = computed(() => (store.darkMode === "dark-mode" ? "Light Mode" : "Dark Mode"));
const toggleDarkMode = () => store.toggleDarkMode();

const dropdownVisible = ref(false);
const router = useRouter();
let currentAnnotation = null;

const isUserLoggedIn = computed(() => !!store.userId && !!store.username);
const toggleDropdown = () => {
  dropdownVisible.value = !dropdownVisible.value;
};
const delayedNavigate = (path) => {
  dropdownVisible.value = false;
  setTimeout(() => {
    router.push(path);
  }, 1000);
};
const delayedLogout = () => {
  dropdownVisible.value = false;
  setTimeout(async () => {
    store.clearUserData();
    await router.push("/login");
  }, 1000);
};
const showUnderline = (event) => {
  const element = event.target;
  const annotation = annotate(element, {
    type: "underline",
    color: "black",
    strokeWidth: 2,
    animationDuration: 500,
    padding: -10,
  });
  if (currentAnnotation) {
    currentAnnotation.remove();
  }
  currentAnnotation = annotation;
  currentAnnotation.show();
};
const hideUnderline = () => {
  if (currentAnnotation) {
    currentAnnotation.remove();
    currentAnnotation = null;
  }
};
const handleClickOutside = (event) => {
  const dropdownElement = document.querySelector(".dropdown");
  const logoElement = document.querySelector(".logo");
  if (
      dropdownVisible.value &&
      dropdownElement &&
      !dropdownElement.contains(event.target) &&
      logoElement &&
      !logoElement.contains(event.target)
  ) {
    dropdownVisible.value = false;
  }
};
const checkUserData = () => {
  store.loadUserData();
  if (!store.userId || !store.username) {
    console.error("UserID oder Benutzername fehlt");
  }
};
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
  checkUserData();
});
onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
<style scoped>
.roll-enter-active {
  animation: rollIn 0.6s ease forwards;
}
.roll-leave-active {
  animation: rollOut 0.6s ease forwards;
}
@keyframes rollIn {
  from {
    transform: scaleY(0);
    opacity: 0;
  }
  to {
    transform: scaleY(1);
    opacity: 1;
  }
}
@keyframes rollOut {
  from {
    transform: scaleY(1);
    opacity: 1;
  }
  to {
    transform: scaleY(0);
    opacity: 0;
  }
}
.navbar {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1000;
}
.logo {
  cursor: pointer;
  height: 20vh;
  margin-bottom: 10px;
  transition: transform 0.3s ease;
}
.logo img {
  max-height: 20vh;
  object-fit: contain;
  border-radius: 50%;
}
.logo:hover {
  transform: scale(1.1);
}
.dropdown {
  position: relative;
  background-image: url('@/assets/pictures/Schriftrolle.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 20px;
  width: 250px;
  text-align: center;
  overflow: hidden;
}
.dropdown h1 {
  color: var(--text-color);
  font-family: 'Viner Hand ITC', cursive;
  font-weight: bold;
}
nav {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  align-items: center;
}
nav a,
.dropdown-button,
.dark-mode-link {
  font-size: 1.2rem;
  text-decoration: none;
  color: var(--text-color);
  padding: 5px 10px;
  background: none;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  width: fit-content;
  text-align: center;
  transition: all 0.3s ease-in-out;
  font-family: 'Viner Hand ITC', cursive;
  font-weight: bold;
}
</style>