import api from './api'
import type { BoardMemberResponse } from '@/types/dto'
import type { PageResponse } from '@/types/page/pageResponse'

export const MemberService = {
    addMember(boardId: number, userId: number) {
        return api.post<BoardMemberResponse>(
            `/board-member/add/board/${boardId}/user/${userId}`
        )
    },
    getMembers(boardId: number, page:number = 0, size:number = 10) {
        return api.get<PageResponse<BoardMemberResponse>>(`/board-member/board/${boardId}?page=${page}&size=${size}`)
    },
    getMember(boardId: number, userId: number) {
        return api.get<BoardMemberResponse>(
            `/board-member/board/${boardId}/user/${userId}`
        )
    },
    removeMember(boardId: number, userId: number) {
        return api.delete<void>(`/board-member/board/${boardId}/user/${userId}`)
    }
}