import { defineStore } from 'pinia'
import { AuthService} from '@/api/authService'
import type { LoginRequest, LoginResponse } from '@/types/dto'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null as LoginResponse | null,
        account: null as LoginResponse | null,
        loading: false as boolean,
        initialized: false as boolean,
    }),

    getters: {
        isAuthenticated: (state) => !!state.user, 
    },

    actions: {
        async login(payload: LoginRequest) {
            this.loading = true;
            try {
                const { data } = await AuthService.login(payload);

                this.user = data;
                this.account = data;
                localStorage.setItem('account', JSON.stringify(data ));
                return true;
            } catch (err) {
                console.error('Login failed: ', err);
                return false;
            } finally {
                this.loading = false;
            }
        },

        async restoreSession() {
            if (this.initialized) return;

            try {
                const { data } = await AuthService.refresh();
                this.user = data;
                this.account = localStorage.getItem('account') ? JSON.parse(localStorage.getItem('account')!) : null;
            } catch {
                this.user = null;
                localStorage.removeItem('account');
            } finally {
                this.initialized = true;
            }
        },
        
        async logout() {
            this.user = null;
            localStorage.removeItem('account');
            this.account = null;
            this.loading = true;
            await AuthService.logout();
        }
    },
});