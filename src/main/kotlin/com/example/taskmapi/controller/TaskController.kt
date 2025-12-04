package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.enums.TaskStatus
import com.example.taskmapi.service.TaskService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/task")
class TaskController(
    private val taskService: TaskService,
    private val authEmail: AuthEmailUtil
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add/board/{boardId}")
    fun add(@PathVariable boardId: Long, @RequestBody taskReq: TaskRequest): TaskResponse {
        val user = authEmail.getUser()
        return taskService.addTask(boardId, taskReq, user)
    }

    @GetMapping("/board/{boardId}")
    fun retrieveByBoard(@PathVariable boardId: Long, pageable: Pageable): Page<TaskResponse> {
        val user = authEmail.getUser()
        return taskService.retrieveTaskByBoard(boardId, pageable, user)
    }

    @GetMapping("{taskId}/board/{boardId}")
    fun retrieve(@PathVariable boardId: Long, @PathVariable taskId: Long): TaskResponse? {
        val user = authEmail.getUser()
        return taskService.retrieveTaskById(boardId, taskId, user)
    }

    @PutMapping("/{taskId}/board/{boardId}")
    fun update(@PathVariable boardId: Long, @PathVariable taskId: Long, @RequestBody taskReq: TaskUpdateRequest): TaskResponse {
        val user = authEmail.getUser()
        return taskService.updateTask(boardId, taskId, taskReq, user)
    }

    @PatchMapping("/{taskId}/status/{status}/board/{boardId}")
    fun updateStatus(@PathVariable boardId: Long, @PathVariable taskId: Long, @PathVariable status: TaskStatus): TaskResponse {
        val user = authEmail.getUser()
        return taskService.updateStatus(boardId, taskId, status, user)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{taskId}/board/{boardId}")
    fun deleteTask(@PathVariable boardId: Long, @PathVariable taskId: Long): Unit? {
        val user = authEmail.getUser()
        return taskService.deleteTask(boardId, taskId, user)
    }
}