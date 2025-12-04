import api from './api';
import type {
    BoardRequest,
    BoardResponse,
    BoardUpdateRequest
} from '@/types/dto';
import type { PageResponse } from '@/types/page/pageResponse'

export const BoardService = {
    getBoards(page:number = 0, size:number = 10) {
        return api.get<PageResponse<BoardResponse>>(`/board?page=${page}&size=${size}`);
    },

    getBoardsByMember(page: number = 0, size: number = 10) {
        return api.get<PageResponse<BoardResponse>>(`/board/my?page=${page}&size=${size}`);
    },
    getBoard(id: number) {
        return api.get<BoardResponse>(`/board/${id}`)
    },
    createBoard(data: BoardRequest) {
        return api.post<BoardResponse>('/board', data)
    },
    updateBoard(id: number, data: BoardUpdateRequest) {
        return api.put<BoardResponse>(`/board/${id}`, data)
    },
    deleteBoard(id: number) {
        return api.delete<void>(`/board/${id}`)
    }
}