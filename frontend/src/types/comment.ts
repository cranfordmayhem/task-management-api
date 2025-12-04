export interface TaskCommentRequest {
    content: string;
}

export interface TaskCommentResponse {
    id: number;
    content: string;
    authorId: number;
    authorEmail: string;
    createdAt: string;
}