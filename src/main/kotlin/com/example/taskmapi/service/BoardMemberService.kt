package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.*
import com.example.taskmapi.entity.enums.BoardRole
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.*
import com.example.taskmapi.utils.AuthEmailUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BoardMemberService(
    private val memberRepo: BoardMemberRepository,
    private val boardRepo: BoardRepository,
    private val authEmail: AuthEmailUtil,
    private val userAccRepo: UserAccountRepository
) {

    private val logger = LoggerFactory.getLogger(BoardMemberService::class.java)

    fun addMember(boardId: Long, userId: Long, userEmail: String): BoardMemberResponse {
        logger.debug("Adding Member")
        val member = BoardMemberRequest(boardId, userId, BoardRole.MEMBER)
        val memberUser = userAccRepo.findById(userId)
            .orElseThrow { IdNotFoundException(userId) }
        val owner = authEmail.checkUser(userEmail)
        val board =authEmail.getBoard(boardId)
        if(board.owner.id != owner.id)
            throw UnauthorizedException("add board members t ", board.id)
        return memberRepo.save(member.toEntity(board, memberUser)).toResponse()
            .also { logger.info("Member added successfully") }
    }

    fun addOwner(boardId: Long, userId: Long, role: BoardRole, userEmail: String): BoardMemberResponse {
        logger.debug("Adding Owner")
        val member = BoardMemberRequest(boardId, userId, role)
        val user = authEmail.checkUser(userEmail)
        val board =authEmail.getBoard(boardId)
        if(board.owner.id != user.id)
            throw UnauthorizedException("add board members to ", board.id)
        return memberRepo.save(member.toEntity(board, user)).toResponse()
            .also { logger.info("Owner added successfully") }
    }

    fun retrieveByBoardId(boardId: Long, pageable: Pageable): Page<BoardMemberResponse> {
        logger.debug("Retrieving Members in the board")
        return memberRepo.findByBoardId(boardId, pageable)
            .map { it.toResponse() }
            .also { logger.info("Members of $boardId retrieved successfully") }
    }

    fun retrieveById(boardId: Long, userId: Long): BoardMemberResponse =
        memberRepo.findByBoardIdAndUserId(boardId, userId)?.apply {
            logger.debug("Retrieving Board Member information")
        }?.toResponse().also {
            logger.info("Board Member Information Retrieved Successfully")
        } ?: throw MemberNotFoundException()

    fun removeMember(boardId: Long, userId: Long, userEmail: String): Unit? =
        memberRepo.findByBoardIdAndUserId(boardId, userId)?.apply {
            val user = authEmail.checkUser(userEmail)
            val board = boardRepo.findById(boardId)
                .orElseThrow { IdNotFoundException(boardId) }
            if(board.owner.id != user.id)
                throw RuntimeException("Only board owners can remove members")
            if(board.owner.id == user.id)
                throw RuntimeException("Cannot remove themselves")
        }?.let {
            memberRepo.deleteByBoardIdAndUserId(boardId, userId)
        } ?: throw MemberNotFoundException()

}