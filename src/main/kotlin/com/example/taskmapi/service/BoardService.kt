package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.enums.BoardRole
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.BoardRepository
import com.example.taskmapi.utils.AuthEmailUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val boardRepo: BoardRepository,
    private val memberService: BoardMemberService,
    private val authEmail: AuthEmailUtil,
) {
    private val logger = LoggerFactory.getLogger(BoardService::class.java)

    fun create(boardReq: BoardRequest, userEmail: String): BoardResponse {
        logger.debug("Creating board")
        val user = authEmail.checkUser(userEmail)
        val board = boardRepo.save(boardReq.toEntity(user))
        memberService.addOwner(board.id, user.id, BoardRole.OWNER, user.email)

        return board.toResponse().also { logger.info("Board created successfully") }
    }

    fun retrieve(pageable: Pageable): Page<BoardResponse>{
        logger.debug("Fetching all Board")
        return boardRepo.findAll(pageable).map { it.toResponse() }
            .also { logger.info("Board retrieved successfully") }
    }

    fun retrieveByMember(userEmail: String, pageable: Pageable): Page<BoardResponse> {
        logger.debug("Retrieving all boards with user")
        val user = authEmail.checkUser(userEmail)
        return boardRepo.findAllByMembersContaining(user, pageable).map {
            it.toResponse()
        }.also { logger.info("Boards retrieved successfully") }
    }

    fun retrieveById(id: Long, userEmail: String): BoardResponse? =
        boardRepo.findByIdOrNull(id)?.apply{
        }?.toResponse().also{ logger.info("Board details retrieved successfully") }
            ?: throw IdNotFoundException(id)

    fun updateBoard(id: Long, boardReq: BoardUpdateRequest, userEmail: String): BoardResponse {
        val board = boardRepo.findByIdOrNull(id) ?: throw IdNotFoundException(id)
        val user = authEmail.checkUser(userEmail)

        authEmail.verifyUserOwner(id, board.owner.id, user.id, "update")
        // Apply updates
        boardReq.name?.let { board.name = it }
        boardReq.description?.let { board.description = it }

        val updatedBoard = boardRepo.save(board)
        logger.info("Board updated successfully")

        return updatedBoard.toResponse()
    }


    fun delete(id: Long, userEmail: String): Unit? =
        boardRepo.findByIdOrNull(id)?.apply{
            logger.debug("Deleting board")
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUserOwner(id, this.owner.id,
                user.id, "delete")
        }
            ?.let { boardRepo.deleteById(id) }
            ?.also { logger.info("Board deleted successfully") }
            ?: throw IdNotFoundException(id)
}