package com.example.taskmapi.dto

data class BoardRequest(
    val name: String,
    val description: String?
)

data class BoardUpdateRequest(
    val name: String? = null,
    val description: String? = null,
)

data class BoardResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: String,
    val members: List<BoardMemberResponse>,
    val tasks: List<TaskResponse>
)