import type { BoardRole } from './enums/boardRole'
import type { TaskResponse } from "./task";

export interface BoardRequest {
    name: string;
    description?: string | null;
}

export interface BoardUpdateRequest {
    name?: string | null;
    description?: string | null;
}

export interface BoardResponse {
    id: number;
    name: string;
    description?: string | null;
    owner : string;
    members: BoardMemberResponse[];
    tasks: TaskResponse[];
}

export interface BoardMemberRequest {
    boardId: number;
    userId: number;
    role: BoardRole;
}

export interface BoardMemberResponse {
    userId: number;
    email: string;
    roles: BoardRole;
}