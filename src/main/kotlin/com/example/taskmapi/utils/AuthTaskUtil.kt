package com.example.taskmapi.utils

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.*
import com.example.taskmapi.entity.enums.*
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.*
import org.springframework.stereotype.Component


@Component
class AuthTaskUtil(
    private val userAccRepo: UserAccountRepository,
    private val memberRepo: BoardMemberRepository
) {
    fun checkUser(userEmail: String): UserAccount {
        val user = userAccRepo.findByEmail(userEmail)
            ?: throw UserNotFoundException(userEmail)
        return user
    }

    fun verifyOwnerMember(boardId: Long, ownerId: Long, userId: Long, action: String) {
        val isOwner = ownerId == userId
        val member = memberRepo.findByBoardIdAndUserId(boardId, userId)
        val isMember = member != null

        if(!isOwner && !isMember)
            throw UnauthorizedException(action, boardId)
    }
}