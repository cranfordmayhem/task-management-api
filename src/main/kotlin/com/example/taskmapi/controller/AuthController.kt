package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.service.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.*
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthenticationService,
    private val userAccService: UserAccountService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@Valid @RequestBody userAcc: UserAccountRequest) =
        userAccService.add(userAcc)

    @PostMapping("/login")
    fun login(
        @RequestBody loginDetails: LoginRequest,
        response: HttpServletResponse
    ): LoginResponse =
        authService.authenticate(loginDetails, response)

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse) = authService.logout(response)

    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest,
                response: HttpServletResponse
    ): TokenResponse {
        val refreshToken = request.cookies
            ?. firstOrNull { it.name == "refreshToken" }
            ?. value
            ?: throw RuntimeException("Refresh token missing")
        return TokenResponse(accessToken =
            authService.refreshAccessToken(refreshToken, response))
    }
}