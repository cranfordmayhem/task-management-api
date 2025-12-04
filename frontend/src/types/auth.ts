import type { Role } from './enums/role'

export interface Login {
    username: string;
    password: string;
}

export interface LoginResponse {
    id: number;
    username: string;
    role: Role;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface TokenResponse {
    accessToken: string;
}