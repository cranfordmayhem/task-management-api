package com.example.taskmapi.dto

import com.example.taskmapi.entity.enums.Role

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Long,
    val username: String,
    val role: Role
)