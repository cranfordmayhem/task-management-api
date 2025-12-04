package com.example.taskmapi.config

import com.example.taskmapi.service.AuthenticationService
import com.example.taskmapi.service.TokenService
import com.example.taskmapi.utils.TokenToCookieUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1)
class RefreshTokenFilter(
    private val authService: AuthenticationService,
    private val tokenToCookieUtil: TokenToCookieUtil,
    private val tokenService: TokenService,
    @Value("\${jwt.accessTokenExpiration}") private val accessTokenExpiration: Long = 0
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RefreshTokenFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug("Refreshing token...")
        val accessTokenCookie = request.cookies?.firstOrNull { it.name == "accessToken" }
        val refreshTokenCookie = request.cookies?. firstOrNull { it.name == "refreshToken" }

        var wrappedRequest = request

        if((accessTokenCookie == null || !tokenService.isTokenValid(accessTokenCookie.value))
            &&refreshTokenCookie != null
        ) {
            try {
                val newAccessToken = authService.refreshAccessToken(refreshTokenCookie.value, response)

                log.debug("Generated new access token via refresh token")

                tokenToCookieUtil.toHeader("accessToken", newAccessToken, accessTokenExpiration, response)

                wrappedRequest = object : HttpServletRequestWrapper(request) {
                    override fun getCookies(): Array<Cookie> {
                        val existing = request.cookies?.toMutableList() ?: mutableListOf()
                        existing.removeIf { it.name == "accessToken" }
                        existing.add(Cookie("accessToken", newAccessToken))
                        return existing.toTypedArray()
                    }
                }
            } catch (ex: Exception) {
                log.warn("Failed to refresh access token: ${ex.message}")
            }
        }

        filterChain.doFilter(wrappedRequest, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/auth/login")
    }
}