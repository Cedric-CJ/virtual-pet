import { defineStore } from 'pinia';
import { PetData } from '@/types';

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
            userId: null,
            dead: false // Neu hinzugefügt
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
                userId: null,
                dead: false // Neu hinzugefügt
            };
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
        },
        updatePetData(petData: PetData) {
            this.petData = { ...petData };
        },
        updatePetStat(stat: keyof PetData['stats'], value: number) {
            this.petData.stats = { ...this.petData.stats, [stat]: value };
        }
    }
});