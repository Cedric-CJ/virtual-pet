import { createRouter, createWebHashHistory } from 'vue-router';
import HaustierErstellungsseite from '@/components/Haustier-Erstellungsseite.vue';
import HaustierSeite from '@/components/Haustier-Seite.vue';
import Anmelde_und_Registrierungsseite from '@/components/Anmelde_und_Registrierungsseite.vue';
import AllPets from '@/components/ALLPetsSeite.vue';

import { useUserStore } from "@/store";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
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
      path: '/logout',
      redirect: '/login',
      beforeEnter: (to, from, next) => {
        const userStore = useUserStore();
        userStore.clearUserData();
        next();
      }
    },
    {
      path: '/pet',
      name: 'Pet',
      component: HaustierSeite
    },
    {
      path: '/All',
      name: 'All',
      component: AllPets
    }]
});

export default router;