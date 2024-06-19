import { createRouter, createWebHashHistory } from 'vue-router';
import HaustierErstellungsseite from '@/components/Haustier-Erstellungsseite.vue';
import HaustierSeite2 from '@/components/Haustier-Seite2.vue';
import Anmelde_und_Registrierungsseite from '@/components/Anmelde_und_Registrierungsseite.vue';
import { useUserStore } from "@/store";

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/create',
    name: 'Erstellen',
    component: HaustierErstellungsseite
  },
  {
    path: '/login',
    name: 'Login',
    component: Anmelde_und_Registrierungsseite
  },
  {
    path: '/pet',
    name: 'pet',
    component: HaustierSeite2,
    props: route => {
      try {
        return { petData: JSON.parse(decodeURIComponent(route.query.petData)) };
      } catch (error) {
        console.error('Failed to parse petData:', route.query.petData, error);
        return { petData: {} };
      }
    }
  },
  {
    path: '/logout',
    redirect: '/login',
    beforeEnter: (to, from, next) => {
      const userStore = useUserStore();
      userStore.clearUserData();
      next();
    }
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;