package com.example.taskmapi.dto

import com.example.taskmapi.entity.*
import com.example.taskmapi.entity.enums.BoardRole

data class BoardMemberRequest(
    val boardId: Long,
    val userId: Long,
    val role: BoardRole
)

data class BoardMemberResponse(
    val userId: Long,
    val email: String,
    val roles: BoardRole
)