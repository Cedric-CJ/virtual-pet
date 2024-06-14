import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        userId: localStorage.getItem('userId') || ''
    }),
    actions: {
        updateUserId(userId: string) {
            this.userId = userId;
            localStorage.setItem('userId', userId); // Speichern im localStorage
        }
    },
    getters: {
        getUserId: (state) => state.userId
    }
});