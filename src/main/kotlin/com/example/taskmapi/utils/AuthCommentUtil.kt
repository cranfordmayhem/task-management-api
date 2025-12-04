package com.example.taskmapi.utils

import com.example.taskmapi.entity.*
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component


@Component
class AuthCommentUtil(
    private val userAccRepo: UserAccountRepository,
    private val memberRepo: BoardMemberRepository,
    private val taskRepo: TaskRepository,
    private val boardRepo: BoardRepository
) {
    fun checkUser(userEmail: String, boardId: Long, taskId: Long): AuthCheckResult {
        val user = userAccRepo.findByEmail(userEmail)
            ?: throw UserNotFoundException(userEmail)
        val board = boardRepo.findById(boardId).orElseThrow { IdNotFoundException(boardId) }
        val task = taskRepo.findByIdOrNull(taskId) ?: throw IdNotFoundException(taskId)
        return AuthCheckResult(user, board, task)
    }

    fun verifyOwnerMember(boardId: Long, ownerId: Long, userId: Long, action: String) {
        val isOwner = ownerId == userId
        val member = memberRepo.findByBoardIdAndUserId(boardId, userId)
        val isMember = member != null

        if(!isOwner && !isMember)
            throw UnauthorizedException(action, boardId)
    }
}

data class AuthCheckResult(
    val user: UserAccount,
    val board: Board,
    val task: Task
)