import api from './api'
import type {
    TaskCommentRequest,
    TaskCommentResponse,
} from '@/types/dto'
import type { PageResponse } from '@/types/page/pageResponse'

export const CommentService = {
    addComment(boardId: number, taskId: number, data: TaskCommentRequest) {
        return api.post<TaskCommentResponse>(
            `/comment/board/${boardId}/task/${taskId}`, data
        )
    },
    getComments(boardId: number, taskId: number, page:number = 0, size:number = 10) {
        return api.get<PageResponse<TaskCommentResponse>>(`/comment/board/${boardId}/task/${taskId}?page=${page}&size=${size}`)
    },
    getComment(boardId: number, taskId: number, commentId: number) {
        return api.get<TaskCommentResponse>(
            `/comment/board/${boardId}/task/${taskId}/comment/${commentId}`
        )
    },
    updateComment(
        boardId: number, taskId: number, commentId: number, data: TaskCommentRequest
    ) {
        return api.put<TaskCommentResponse>(
            `/comment/board/${boardId}/task/${taskId}/comment/${commentId}`, data
        )
    }
}