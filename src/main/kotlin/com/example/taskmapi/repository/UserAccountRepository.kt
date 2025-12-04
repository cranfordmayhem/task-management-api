package com.example.taskmapi.repository

import com.example.taskmapi.entity.enums.Role
import com.example.taskmapi.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepository: JpaRepository<UserAccount, Long>{
    fun existsByRole(role: Role): Boolean
    fun findByEmail(email: String): UserAccount?
    fun existsByEmail(email: String): Boolean
}