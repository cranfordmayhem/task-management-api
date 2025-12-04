package com.example.taskmapi.dto

import java.time.Instant

data class TaskCommentRequest(
    val content: String
)

data class TaskCommentResponse(
    val id: Long,
    val content: String,
    val authorId: Long,
    val authorEmail: String,
    val createdAt: Instant
)
