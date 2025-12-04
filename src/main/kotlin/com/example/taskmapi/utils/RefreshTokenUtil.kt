package com.example.taskmapi.utils

import com.example.taskmapi.dto.RefreshTokenRequest
import com.example.taskmapi.dto.toEntity
import com.example.taskmapi.entity.RefreshToken
import com.example.taskmapi.exception.InvalidTokenException
import com.example.taskmapi.repository.RefreshTokenRepository
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

@Component
class RefreshTokenUtil(
    private val refreshTokenRepo: RefreshTokenRepository
) {
    fun hash(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") {"%02x".format(it)}
    }

    fun regenerateAndSaveRefreshToken(
        oldToken: RefreshToken,
        newRefreshToken: String,
        expiration: Long
    ): RefreshToken {
        val newHashedToken = hash(newRefreshToken)
        val newRefreshTokenEntity = RefreshTokenRequest(newHashedToken).toEntity(
            oldToken.userAccount,
            Instant.now().plusMillis(expiration)
        )

        return refreshTokenRepo.save(newRefreshTokenEntity)
    }

    fun isTokenValid(token: RefreshToken) {
        if(token.expiryDate.isBefore(Instant.now()) || token.used) {
            token.used = true
            refreshTokenRepo.save(token)
            throw InvalidTokenException("USED_REFRESH_TOKEN")
        }
    }
}