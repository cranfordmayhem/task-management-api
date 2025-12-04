package com.example.taskmapi.service

import com.example.taskmapi.dto.LoginRequest
import com.example.taskmapi.dto.LoginResponse
import com.example.taskmapi.dto.RefreshTokenRequest
import com.example.taskmapi.dto.toEntity
import com.example.taskmapi.entity.enums.Role
import com.example.taskmapi.exception.AuthenticationUserException
import com.example.taskmapi.exception.InvalidTokenException
import com.example.taskmapi.exception.UserNotFoundException
import com.example.taskmapi.repository.RefreshTokenRepository
import com.example.taskmapi.repository.UserAccountRepository
import com.example.taskmapi.utils.RefreshTokenUtil
import com.example.taskmapi.utils.TokenToCookieUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class AuthenticationService(
    private val userAccountRepo: UserAccountRepository,
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
    private val refreshTokenRepo: RefreshTokenRepository,
    private val refreshTokenUtil: RefreshTokenUtil,
    private val tokenToCookieUtil: TokenToCookieUtil,
    @Value("\${jwt.accessTokenExpiration}") private val accessTokenExpiration: Long = 0,
    @Value("\${jwt.refreshTokenExpiration}") private val refreshTokenExpiration: Long = 0
) {
    private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun authenticate(loginRequest: LoginRequest, response: HttpServletResponse): LoginResponse {
        try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )
        } catch(ex: Exception) {
            logger.error("Login Failed: ${ex.message}")
            throw AuthenticationUserException()
        }

        val user = userDetailsService.loadUserByUsername(loginRequest.username)
        val entityUser = userAccountRepo.findByEmail(user.username)
            ?: throw UserNotFoundException(user.username)

        val accessToken = createAccessToken(user)
        val rawRefreshToken = createRefreshToken(user)
        val hashedRefreshToken = refreshTokenUtil.hash(rawRefreshToken)

        val refreshTokenEntity = RefreshTokenRequest(hashedRefreshToken).toEntity(
            entityUser,
            Instant.now().plusMillis(refreshTokenExpiration)
        ).apply {
            createdBy = entityUser.email
        }
        refreshTokenRepo.save(refreshTokenEntity)

        tokenToCookieUtil.toHeader(
            "accessToken", accessToken,
            accessTokenExpiration, response
        )
        tokenToCookieUtil.toHeader(
            "refreshToken", rawRefreshToken,
            refreshTokenExpiration, response
        )

        val role = determineRole(user)

        return LoginResponse(entityUser.id, user.username, role)
    }

    fun refreshAccessToken(rawRefreshToken: String, response: HttpServletResponse): String {
        val username = tokenService.extractUsername(rawRefreshToken)
        val hashedToken = refreshTokenUtil.hash(rawRefreshToken)

        val refreshTokenEntity = refreshTokenRepo.findByToken(hashedToken)
            .orElseThrow { InvalidTokenException("REFRESH_TOKEN_NOT_FOUND") }

        refreshTokenUtil.isTokenValid(refreshTokenEntity)

        val currentUserDetails = userDetailsService.loadUserByUsername(username)
        if(currentUserDetails.username != refreshTokenEntity.userAccount.email) {
            throw InvalidTokenException("INVALID_SIGNATURE")
        }

        refreshTokenEntity.used = true
        refreshTokenRepo.save(refreshTokenEntity)

        val newRefreshToken = createRefreshToken(currentUserDetails)
        refreshTokenUtil.regenerateAndSaveRefreshToken(refreshTokenEntity, newRefreshToken, refreshTokenExpiration)
        val newAccessToken = createAccessToken(currentUserDetails)

        tokenToCookieUtil.toHeader("accessToken", newAccessToken,
            accessTokenExpiration, response)
        tokenToCookieUtil.toHeader("refreshToken", newRefreshToken,
            refreshTokenExpiration, response)

        return newAccessToken
    }

    fun logout(response: HttpServletResponse): String {
        tokenToCookieUtil.clearCookies(response)
        return "Logged out successfully"
    }

    private fun createAccessToken(user: UserDetails): String {
        return tokenService.generateToken(
            subject = user.username,
            expiration = Date(System.currentTimeMillis() + accessTokenExpiration),
            additionalClaims = mapOf("nonce" to UUID.randomUUID().toString())
        )
    }

    private fun createRefreshToken(user: UserDetails) = tokenService.generateToken(
        subject = user.username,
        expiration = Date(System.currentTimeMillis() + refreshTokenExpiration),
        additionalClaims = mapOf("nonce" to UUID.randomUUID().toString())
    )

    private fun determineRole(user: UserDetails) = when {
        user.authorities.any { it.authority == "ROLE_ADMIN" } -> Role.ADMIN
        else -> Role.USER
    }
}