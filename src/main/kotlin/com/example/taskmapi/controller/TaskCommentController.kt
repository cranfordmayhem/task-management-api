package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.service.TaskCommentService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comment")
class TaskCommentController(
    private val commentService: TaskCommentService,
    private val authEmail: AuthEmailUtil
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/board/{boardId}/task/{taskId}")
    fun add(
        @PathVariable boardId: Long,
        @PathVariable taskId: Long,
        @RequestBody commentReq: TaskCommentRequest
    ): TaskCommentResponse {
        val user = authEmail.getUser()
        return commentService.create(boardId, taskId, commentReq, user)
    }

    @GetMapping("/board/{boardId}/task/{taskId}")
    fun retrieve(
        @PathVariable boardId: Long,
        @PathVariable taskId: Long,
        pageable: Pageable
    ): Page<TaskCommentResponse> {
        val user = authEmail.getUser()
        return commentService.retrieveByTask(boardId, taskId, user, pageable)
    }

    @GetMapping("/board/{boardId}/task/{taskId}/comment/{commentId}")
    fun retrieveById(
        @PathVariable boardId: Long,
        @PathVariable taskId: Long,
        @PathVariable commentId: Long
    ): TaskCommentResponse? {
        val user = authEmail.getUser()
        return commentService.retrieveById(boardId, taskId, commentId, user)
    }

    @PutMapping("/board/{boardId}/task/{taskId}/comment/{commentId}")
    fun update(
        @PathVariable boardId: Long,
        @PathVariable taskId: Long,
        @PathVariable commentId: Long,
        @RequestBody commentReq: TaskCommentRequest
    ): TaskCommentResponse {
        val user = authEmail.getUser()
        return commentService.update(boardId, taskId, commentId, commentReq, user)
    }

    @DeleteMapping("/board/{boardId}/task/{taskId}/comment/{commentId}")
    fun delete(
        @PathVariable boardId: Long,
        @PathVariable taskId: Long,
        @PathVariable commentId: Long,
    ): Unit? {
        val user = authEmail.getUser()
        return commentService.delete(boardId, taskId, commentId, user)
    }

}