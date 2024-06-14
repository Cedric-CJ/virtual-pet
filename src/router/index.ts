import { createRouter, createWebHashHistory } from 'vue-router';
import HaustierErstellungsseite from '@/components/Haustier-Erstellungsseite.vue';
import HaustierSeite2 from '@/components/Haustier-Seite2.vue';
import Anmelde_und_Registrierungsseite from '@/components/Anmelde_und_Registrierungsseite.vue';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
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
      name: 'Dein Haustier',
      component: HaustierSeite2
    }
  ]
});

export default router;