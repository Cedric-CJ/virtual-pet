import { defineStore } from 'pinia';
import { PetData } from '@/types';

export const useUserStore = defineStore('user', {
    state: (): {
        userId: string;
        username: string;
        petData: PetData;
        darkMode: string;
    } => ({
        userId: '',
        username: '',
        petData: {
            name: '',
            type: '',
            stats: {
                Energie: 0,
                Hunger: 0,
                Durst: 0,
                Komfort: 0
            },
            createdDate: '',
            lastFed: '',
            lastWatered: '',
            lastSlept: '',
            lastPetted: '',
            lastShowered: '',
            username: '',
            userId: null,
            dead: false
        },
        darkMode: localStorage.getItem("mode") || "light-mode" // Default "light-mode"
    }),
    actions: {
        // Dark Mode umschalten
        toggleDarkMode() {
            this.darkMode = this.darkMode === "dark-mode" ? "light-mode" : "dark-mode";
            localStorage.setItem("mode", this.darkMode);
            document.body.classList.remove("dark-mode", "light-mode");
            document.body.classList.add(this.darkMode);
        },

        // User ID aktualisieren und speichern
        updateUserId(userId: string) {
            this.userId = userId;
            localStorage.setItem('userId', userId);
        },

        // Benutzername aktualisieren und speichern
        updateUsername(username: string) {
            this.username = username;
            localStorage.setItem('username', username);
        },

        // Daten aus `localStorage` laden
        loadUserData() {
            this.userId = localStorage.getItem('userId') || '';
            this.username = localStorage.getItem('username') || '';
            this.darkMode = localStorage.getItem("mode") || "light-mode";
            document.body.classList.add(this.darkMode); // Direkt beim Laden anwenden
        },

        // Benutzer- und Pet-Daten zurücksetzen
        clearUserData() {
            this.userId = '';
            this.username = '';
            this.petData = {
                name: '',
                type: '',
                stats: {
                    Energie: 0,
                    Hunger: 0,
                    Durst: 0,
                    Komfort: 0
                },
                createdDate: '',
                lastFed: '',
                lastWatered: '',
                lastSlept: '',
                lastPetted: '',
                lastShowered: '',
                username: '',
                userId: null,
                dead: false
            };
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
        },

        // Pet-Daten aktualisieren
        updatePetData(petData: PetData) {
            this.petData = { ...petData };
        },

        // Einzelnen Pet-Stat aktualisieren
        updatePetStat(stat: keyof PetData['stats'], value: number) {
            this.petData.stats = { ...this.petData.stats, [stat]: value };
        }
    }
});