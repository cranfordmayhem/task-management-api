package com.example.taskmapi.repository

import com.example.taskmapi.entity.TaskComment
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskCommentRepository: JpaRepository<TaskComment, Long>{
    fun findAllByTaskId(taskId: Long, pageable: Pageable): Page<TaskComment>
}
