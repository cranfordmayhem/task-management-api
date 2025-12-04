package com.example.taskmapi.config

import com.example.taskmapi.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(2)
class JwtAuthorizationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            var token: String? = null

            token = request.cookies?.firstOrNull { it.name == "accessToken" }?.value

            val header = request.getHeader("Authorization")
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substringAfter("Bearer ")
            }

            if(token != null && tokenService.isTokenValid(token)) {
                val username = tokenService.extractUsername(token)
                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, "Bearer $token", userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            } else if (token != null) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json"
                response.writer.write("""{"error":"Invalid or expired token"}""")
                return
            }
        } catch (ex: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("""{"error":"${ex.message ?: "unknown"}"}""")
            return
        }

        filterChain.doFilter(request, response)
    }
}