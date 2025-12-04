import api from './api'
import type {
    TaskRequest,
    TaskResponse,
    TaskUpdateRequest,
    TaskStatus
} from '@/types/dto'
import type { PageResponse } from '@/types/page/pageResponse'

export const TaskService = {
    addTask(boardId: number, data: TaskRequest) {
        return api.post<TaskResponse>(`task/add/board/${boardId}`, data)
        .then(res => res.data);
    },

    getTasks(boardId: number, page:number = 0, size:number = 10) {
        return api.get<PageResponse<TaskResponse>>(`/task/board/${boardId}?page=${page}&size=${size}`)
        .then(res => res.data);
    },
    getTask(boardId: number, taskId: number) {
        return api.get<TaskResponse>(`/task/${taskId}/board/${boardId}`)
        .then(res => res.data);
    },
    updateTask(boardId: number, taskId: number, data: TaskUpdateRequest) {
        return api.put<TaskResponse>(`/task/${taskId}/board/${boardId}`, data)
        .then(res => res.data);
    },
    updateStatus(boardId: number, taskId: number, status: TaskStatus) {
        return api.patch<TaskResponse>(`/task/${taskId}/status/${status}/board/${boardId}`)
        .then(res => res.data);
        
    },
    deleteTask(boardId: number, taskId: number) {
        return api.delete<void>(`/task/${taskId}/board/${boardId}`)
        .then(res => res.data);
    }
}