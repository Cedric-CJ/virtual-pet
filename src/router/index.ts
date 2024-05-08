import { createRouter, createWebHistory } from 'vue-router'
import Anmeldeseite from '@/components/Anmeldeseite.vue'
import HaustierErstellungsseite from '@/components/Haustier-Erstellungsseite.vue'
import Registrierungsseite from '@/components/Registrierungsseite.vue'
import HaustierSeite from '@/components/Haustier-Seite.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/create',
      name: 'Erstellen',
      component: HaustierErstellungsseite
    },
    {
      path: '/login',
      name: 'Login',
      component: Anmeldeseite
    },
    {
      path: '/registration',
      name: 'Registration',
      component: Registrierungsseite
    },
    {
      path: '/pet',
      name: 'Dein Haustier',
      component: HaustierSeite
    }
  ]
})

export default router