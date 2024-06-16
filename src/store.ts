import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        userId: '',
        username: ''
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
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
        }
    }
});