package com.example.taskmapi.repository

import com.example.taskmapi.entity.Board
import com.example.taskmapi.entity.UserAccount
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository: JpaRepository<Board, Long>{
    fun findByOwnerId(userId: Long): Board?
    @EntityGraph(attributePaths = ["owner"])
    @Query("""
        select distinct b
        from Board b
        join b.members m
        where m.user = :user
    """)
    fun findAllByMembersContaining(
        user: UserAccount,
        pageable: Pageable
    ): Page<Board>
}