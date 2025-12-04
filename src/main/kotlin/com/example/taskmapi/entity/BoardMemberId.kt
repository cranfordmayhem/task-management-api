package com.example.taskmapi.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BoardMemberId(
    val boardId: Long,
    val userId: Long
) : Serializable