package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.*
import com.example.taskmapi.utils.AuthCommentUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskCommentService(
    private val commentRepo: TaskCommentRepository,
    private val taskRepo: TaskRepository,
    private val boardRepo: BoardRepository,
    private val authComment: AuthCommentUtil
) {

    private val logger = LoggerFactory.getLogger(TaskCommentService::class.java)

    fun create(boardId: Long, taskId: Long, commentReq: TaskCommentRequest, userEmail: String):
            TaskCommentResponse {
        logger.debug("Creating comment")
        val auth = authComment.checkUser(userEmail, boardId, taskId)

        authComment.verifyOwnerMember(boardId, auth.board.owner.id, auth.user.id, "create")

        return commentRepo.save(commentReq.toEntity(auth.task, auth.user)).toResponse()
            .also{ logger.info("Comment created successfully") }
    }

    fun retrieveByTask(boardId: Long, taskId: Long, userEmail: String, pageable: Pageable):
            Page<TaskCommentResponse> {
        val auth = authComment.checkUser(userEmail, boardId, taskId)

        authComment.verifyOwnerMember(boardId, auth.board.owner.id, auth.user.id, "retrieve")

        return commentRepo.findAllByTaskId(taskId, pageable).map { it.toResponse() }
            .also{ logger.info("Comments under task $taskId retrieved successfully") }
    }

    fun retrieveById(boardId: Long, taskId: Long, commentId: Long, userEmail: String): TaskCommentResponse? {
        logger.debug("Retrieving comment with ID $commentId")
        val auth = authComment.checkUser(userEmail, boardId, taskId)

        authComment.verifyOwnerMember(boardId, auth.board.owner.id, auth.user.id, "retrieve")

        return commentRepo.findByIdOrNull(commentId)?.toResponse()
            ?.apply{ logger.info("Comment with id $commentId retrieved successfully") }
            ?: throw IdNotFoundException(commentId)
    }

    fun update(boardId: Long, taskId: Long, commentId: Long, commentReq: TaskCommentRequest, userEmail: String): TaskCommentResponse {
        logger.debug("Updating comment")
        val auth = authComment.checkUser(userEmail, boardId, taskId)

        val comment = commentRepo.findByIdOrNull(commentId) ?: throw IdNotFoundException(commentId)

        if(comment.author != auth.user && auth.board.owner.id != auth.user.id)
            throw UnauthorizedException("update", commentId)

        commentReq.content.let { comment.comment = it }

        val updatedComment = commentRepo.save(comment)
        logger.info("Comment updated successfully")

        return updatedComment.toResponse()
    }

    fun delete(boardId: Long, taskId: Long, commentId: Long, userEmail: String): Unit? {
        logger.debug("Deleting comment")
        val auth = authComment.checkUser(userEmail, boardId, taskId)

        val comment = commentRepo.findByIdOrNull(commentId) ?: throw IdNotFoundException(commentId)

        if(comment.author != auth.user && auth.board.owner.id != auth.user.id)
            throw UnauthorizedException("delete", commentId)

        return commentRepo.deleteById(commentId)
            .also{ logger.info("Comment deleted successfully") }
    }

}