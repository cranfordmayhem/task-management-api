package com.example.taskmapi.dto

import com.example.taskmapi.entity.UserAccount
import com.example.taskmapi.entity.enums.TaskStatus

data class TaskRequest(
    val title: String,
    val description: String?
)

data class TaskUpdateRequest(
    val title: String?,
    val description: String?,
    val assigneeId: Long?,
    val position: Int?
)

data class TaskStatusUpdateRequest(
    val status: TaskStatus
)

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val assignee: String? = null,
    val position: Int,
    val comments: List<TaskCommentResponse>,
    val boardId: Long
)

data class TaskUpdateMessage(
    val boardId: Long,
    val taskId: Long,
    val action: String, // "CREATE", "UPDATE", "DELETE"
    val payload: Any?
)
