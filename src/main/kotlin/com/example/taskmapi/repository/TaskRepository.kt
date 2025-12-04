package com.example.taskmapi.repository

import com.example.taskmapi.entity.Task
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: JpaRepository<Task, Long> {
    fun findByBoardId(boardId: Long, pageable: Pageable): Page<Task>
}