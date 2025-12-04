package com.example.taskmapi.entity

import com.example.taskmapi.entity.enums.BoardRole
import jakarta.persistence.*

@Entity
@Table(name="board_members")
data class BoardMember(
    @EmbeddedId
    val id: BoardMemberId,

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("boardId")
    val board: Board,

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("userId")
    val user: UserAccount,

    @Enumerated(EnumType.STRING)
    val role: BoardRole = BoardRole.MEMBER
)