export interface UserAccountRequest {
    email: string;
    password: string;
}

export interface UserAccountResponse {
    id: number;
    email: string;
    role: Role;
}

export interface UserProfileRequest {
    firstName: string;
    lastName: string;
    age: number;
}

export interface UserProfileResponse {
    id: number;
    firstName: string;
    lastName: string;
    age: number;
    email: string;
}

export type Role = "USER" | "ADMIN";