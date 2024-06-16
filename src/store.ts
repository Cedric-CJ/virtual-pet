import { defineStore } from 'pinia';
import { PetData } from '@/types';  // Stelle sicher, dass der Pfad zur types.ts-Datei korrekt ist

export const useUserStore = defineStore('user', {
    state: (): { userId: string; username: string; petData: PetData } => ({
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
            userId: null
        }
    }),
    actions: {
        updateUserId(userId: string) {
            this.userId = userId;
            localStorage.setItem('userId', userId);
        },
        updateUsername(username: string) {
            this.username = username;
            localStorage.setItem('username', username);
        },
        loadUserData() {
            this.userId = localStorage.getItem('userId') || '';
            this.username = localStorage.getItem('username') || '';
        },
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
                userId: null
            };
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
        },
        updatePetData(petData: PetData) {
            this.petData = petData;
        }
    }
});