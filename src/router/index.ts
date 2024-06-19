import { createRouter, createWebHashHistory, RouteRecordRaw, RouteLocationNormalized, NavigationGuardNext } from 'vue-router';
import HaustierErstellungsseite from '@/components/Haustier-Erstellungsseite.vue';
import HaustierSeite2 from '@/components/Haustier-Seite2.vue';
import Anmelde_und_Registrierungsseite from '@/components/Anmelde_und_Registrierungsseite.vue';
import ALLPetsSeite from '@/components/ALLPetsSeite.vue';
import { useUserStore } from "@/store";

const routes: Array<RouteRecordRaw> = [
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
    props: (route: RouteLocationNormalized) => {
      try {
        return { petData: JSON.parse(decodeURIComponent(route.query.petData as string)) };
      } catch (error) {
        console.error('Failed to parse petData:', route.query.petData, error);
        return { petData: {} };
      }
    }
  },
  {
    path: '/dead',
    name: 'ALLPetsSeite',
    component: ALLPetsSeite
  },
  {
    path: '/logout',
    redirect: '/login',
    beforeEnter: (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
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