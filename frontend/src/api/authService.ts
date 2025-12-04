import api from './api';
import type { 
    UserAccountRequest,
    UserAccountResponse,
    LoginRequest,
    LoginResponse,
    TokenResponse
} from '@/types/dto'

export const AuthService = {
    register(data: UserAccountRequest) {
        return api.post<UserAccountResponse>('/auth/register', data)
    },
    login(data: LoginRequest) {
        return api.post<LoginResponse> ('/auth/login', data);
    },

    logout() {
        return api.get('/auth/logout');
    },

    refresh() {
        return api.post<TokenResponse>('/auth/refresh');
    }
};

