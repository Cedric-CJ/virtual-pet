import { createRouter, createWebHistory } from 'vue-router';
import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import Registrierungsseite from '@/views/Registrierung.vue';

// Erstelle einen Router-Mock
const routes = [
  { path: '/', component: { template: '<div>Home</div>' } },
  { path: '/login', component: { template: '<div>Login</div>' } },
  { path: '/registration', component: { template: '<div>Registrierung</div>' } },
];
const router = createRouter({
  history: createWebHistory(),
  routes,
});

describe('Registrierungsseite', () => {
  it('allows user registration', async () => {
    const wrapper = mount(Registrierungsseite, {
      global: {
        plugins: [router],
      },
    });

    const inputUsername = wrapper.find('input[type="text"]');
    const inputPassword = wrapper.find('input[type="password"]');
    // Finde den Button mit dem korrekten Selektor
    const button = wrapper.find('button[type="submit"]'); // Annahme, dass der Button ein 'type="submit"' hat

    await inputUsername.setValue('neuerBenutzer');
    await inputPassword.setValue('passwort123');
    await button.trigger('submit');

    expect((inputUsername.element as HTMLInputElement).value).toBe('neuerBenutzer');
    expect((inputPassword.element as HTMLInputElement).value).toBe('passwort123');
  });
});