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
</template>
<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import { annotate } from "rough-notation";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store";

// Wir definieren einen Typ für die Annotation, den rough-notation zurückgibt
interface Annotation {
  remove: () => void;
  show: () => void;
}

const store = useUserStore();
const darkModeText = computed(() => (store.darkMode === "dark-mode" ? "Light Mode" : "Dark Mode"));
const toggleDarkMode = (): void => store.toggleDarkMode();

const dropdownVisible = ref<boolean>(false);
const router = useRouter();
let currentAnnotation: Annotation | null = null;

const isUserLoggedIn = computed(() => !!store.userId && !!store.username);
const toggleDropdown = (): void => {
  dropdownVisible.value = !dropdownVisible.value;
};

const delayedNavigate = (path: string): void => {
  dropdownVisible.value = false;
  setTimeout(() => {
    router.push(path);
  }, 1000);
};

const delayedLogout = (): void => {
  dropdownVisible.value = false;
  setTimeout(async () => {
    store.clearUserData();
    await router.push("/login");
  }, 1000);
};

const showUnderline = (event: MouseEvent): void => {
  const element = event.target as HTMLElement;
  const annotation = annotate(element, {
    type: "underline",
    color: "black",
    strokeWidth: 2,
    animationDuration: 500,
    padding: -10,
  }) as Annotation;
  if (currentAnnotation) {
    currentAnnotation.remove();
  }
  currentAnnotation = annotation;
  currentAnnotation.show();
};

const hideUnderline = (): void => {
  if (currentAnnotation) {
    currentAnnotation.remove();
    currentAnnotation = null;
  }
};

const handleClickOutside = (event: MouseEvent): void => {
  const dropdownElement = document.querySelector(".dropdown") as HTMLElement | null;
  const logoElement = document.querySelector(".logo") as HTMLElement | null;
  if (
      dropdownVisible.value &&
      dropdownElement &&
      !dropdownElement.contains(event.target as Node) &&
      logoElement &&
      !logoElement.contains(event.target as Node)
  ) {
    dropdownVisible.value = false;
  }
};

const checkUserData = (): void => {
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