package com.example.taskmapi.dto

data class UserProfileRequest(
    val firstName: String,
    val lastName: String,
    val age: Int
)

data class UserProfileResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val email: String
)