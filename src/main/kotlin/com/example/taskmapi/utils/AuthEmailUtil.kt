package com.example.taskmapi.utils

import com.example.taskmapi.dto.toResponse
import com.example.taskmapi.entity.*
import com.example.taskmapi.entity.enums.BoardRole
import com.example.taskmapi.entity.enums.Role
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.BoardMemberRepository
import com.example.taskmapi.repository.BoardRepository
import com.example.taskmapi.repository.UserAccountRepository
import com.example.taskmapi.service.BoardMemberService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthEmailUtil(
    private val userAccRepo: UserAccountRepository,
    private val boardRepo: BoardRepository,
    private val memberRepo: BoardMemberRepository
) {
    fun checkUser(userEmail: String): UserAccount {
        val user = userAccRepo.findByEmail(userEmail)
            ?: throw UserNotFoundException(userEmail)
        return user
    }

    fun getBoard(boardId: Long): Board {
        val board = boardRepo.findByIdOrNull(boardId)
            ?: throw IdNotFoundException(boardId)
        return board
    }

    fun verifyUserOwner(boardId: Long, ownerId: Long , userId: Long, action: String) {
        val member = memberRepo.findByBoardIdAndUserId(boardId, userId)
            ?.toResponse()
            ?: throw MemberNotFoundException()
        if(ownerId != userId && member.roles !in listOf(BoardRole.OWNER))
            throw UnauthorizedException(action, boardId)

        throw UnauthorizedException(action, boardId)
    }

    fun verifyUserMember(boardId: Long, userId: Long, action: String) {
        val member = memberRepo.findByBoardIdAndUserId(boardId, userId)
            ?.toResponse()
            ?:throw MemberNotFoundException()
        if(member.roles != BoardRole.MEMBER)
            throw UnauthorizedException(action, boardId)
    }

    fun verifyUser(id: Long, currentEmail: String, user: UserAccount, action: String) {
        if(currentEmail != user.email && user.role != Role.ADMIN)
            throw UnauthorizedException(action, id)
    }

    fun getUser(): String {
        val email = SecurityContextHolder.getContext().authentication.name
            ?: UnauthenticatedException()
        return email.toString()
    }

    fun retrieveBoard(userId: Long): Long {
        val board = memberRepo.findByUserId(userId)
            ?: throw MemberNotFoundException()
        return board.board.id
    }
}