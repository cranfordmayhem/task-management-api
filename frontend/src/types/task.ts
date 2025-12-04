import type { TaskCommentResponse } from './comment'

export interface TaskRequest {
    title: string;
    description?: string | null;
}

export interface TaskUpdateRequest {
    title?: string | null;
    description?: string | null;
    assigneeId?: number | null;
    position?: number | null;
}

export type TaskStatus = "TODO" | "IN_PROGRESS" | "DONE";

export interface TaskResponse {
    id: number;
    title: string;
    description?: string | null;
    status: TaskStatus;
    assignee?: string | null;
    position: number;
    comments: TaskCommentResponse[];
    boardId: number;
}