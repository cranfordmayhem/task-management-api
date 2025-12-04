package com.example.taskmapi.repository

import com.example.taskmapi.entity.BoardMember
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardMemberRepository: JpaRepository<BoardMember, Long>{
    fun findByBoardId(boardId: Long, pageable: Pageable): Page<BoardMember>
    fun findByBoardIdAndUserId(boardId: Long, userId: Long): BoardMember?
    fun findByUserId(userId: Long): BoardMember?
    fun deleteByBoardIdAndUserId(boardId: Long, userId: Long)
}