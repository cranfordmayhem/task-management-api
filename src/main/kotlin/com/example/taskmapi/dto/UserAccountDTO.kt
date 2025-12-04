package com.example.taskmapi.dto

import com.example.taskmapi.entity.enums.Role

data class UserAccountRequest(
    val email: String,
    val password: String
)

data class AccountUpdateRequest(
    val email: String,
    val password: String,
    val role: Role
)

data class UserAccountResponse(
    val id: Long,
    val email: String,
    val role: Role
)