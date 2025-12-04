package com.example.taskmapi.repository

import com.example.taskmapi.entity.RefreshToken
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional

@Repository
interface RefreshTokenRepository: JpaRepository<RefreshToken, Long>{
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.used = true OR t.expiryDate < :date")
    fun deleteExpiredOrUsedTokens(date: Instant): Int
    fun findByToken(token: String): Optional<RefreshToken>
}