package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.enums.TaskStatus
import com.example.taskmapi.exception.IdNotFoundException
import com.example.taskmapi.repository.*
import com.example.taskmapi.utils.AuthTaskUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepo: TaskRepository,
    private val boardRepo: BoardRepository,
    private val userAccRepo: UserAccountRepository,
    private val authTask: AuthTaskUtil
) {

    private val logger = LoggerFactory.getLogger(TaskService::class.java)

    private fun validateStatusTransition(current: TaskStatus, next: TaskStatus) {
        val allowedTransitions = mapOf(
            TaskStatus.TODO to setOf(TaskStatus.IN_PROGRESS, TaskStatus.DONE),
            TaskStatus.IN_PROGRESS to setOf(TaskStatus.TODO, TaskStatus.DONE),
            TaskStatus.DONE to setOf(TaskStatus.IN_PROGRESS)
        )

        if (!allowedTransitions[current]!!.contains(next)) {
            throw IllegalStateException("Invalid status transition from $current to $next")
        }
    }


    fun addTask(boardId: Long, taskReq: TaskRequest, userEmail: String): TaskResponse {
        logger.debug("Creating task for $boardId")
        val user = authTask.checkUser(userEmail)

        val board = boardRepo.findById(boardId)
            .orElseThrow { IdNotFoundException(boardId) }

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "create")

        return taskRepo.save(taskReq.toEntity(board)).toResponse()
            .also { logger.info("Task created successfully") }
    }

    fun retrieveTaskByBoard(boardId: Long, pageable: Pageable, userEmail: String): Page<TaskResponse> {
        logger.debug("Retrieving tasks in a board")
        val user = authTask.checkUser(userEmail)
        val board = boardRepo.findById(boardId)
            .orElseThrow { IdNotFoundException(boardId) }

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "retrieve")

        return taskRepo.findByBoardId(boardId, pageable).map { it.toResponse() }
            .also { logger.info("Tasks for board $boardId retrieved successfully") }
    }

    fun retrieveTaskById(boardId: Long, taskId: Long, userEmail: String): TaskResponse? {
        logger.debug("Retrieving task with id $taskId")
        val user = authTask.checkUser(userEmail)
        val board = boardRepo.findById(boardId)
            .orElseThrow { IdNotFoundException(boardId) }

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "retrieve")

        return taskRepo.findByIdOrNull(taskId)?.toResponse()
            .also { logger.info("Task with id $taskId retrieved successfully") }
            ?: throw IdNotFoundException(taskId)
    }

    fun updateTask(boardId: Long, taskId: Long, taskReq: TaskUpdateRequest, userEmail: String): TaskResponse {
        logger.debug("Updating task with id $taskId")
        val user = authTask.checkUser(userEmail)
        val board = boardRepo.findById(boardId) .orElseThrow { IdNotFoundException(boardId) }

        val task = taskRepo.findByIdOrNull(taskId) ?: throw IdNotFoundException(taskId)

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "update")

        taskReq.title?.let { task.title = it }
        taskReq.description?.let { task.description = it }
        taskReq.position?.let { task.position = it }

        taskReq.assigneeId?.let { assigneeId ->
            val assignee = userAccRepo.findByIdOrNull(assigneeId) ?: throw IdNotFoundException(assigneeId)

            authTask.verifyOwnerMember(boardId, board.owner.id, assigneeId, "update")

            task.assignee = assignee
        }

        val updatedTask = taskRepo.save(task)
        logger.info("Task updated successfully")

        return updatedTask.toResponse()
    }

    fun updateStatus(boardId: Long, taskId: Long, status: TaskStatus, userEmail: String):
            TaskResponse{
        logger.debug("Updating Task Status")
        val user = authTask.checkUser(userEmail)
        val board = boardRepo.findById(boardId).orElseThrow { IdNotFoundException(boardId) }
        val task = taskRepo.findByIdOrNull(taskId) ?: throw IdNotFoundException(taskId)

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "update")

        validateStatusTransition(task.status, status)

        task.status = status
        return taskRepo.save(task).toResponse()
            .also{ logger.info("Task status updated successfully") }
    }

    fun deleteTask(boardId: Long, taskId: Long, userEmail: String): Unit? {
        logger.debug("Deleting task with id $taskId")
        val user = authTask.checkUser(userEmail)
        val board = boardRepo.findById(boardId).orElseThrow { IdNotFoundException(boardId) }
        taskRepo.findByIdOrNull(taskId) ?: throw IdNotFoundException(taskId)

        authTask.verifyOwnerMember(boardId, board.owner.id, user.id, "delete")

        return taskRepo.deleteById(taskId)
            .also { logger.info("Task deleted successfully") }
    }

}