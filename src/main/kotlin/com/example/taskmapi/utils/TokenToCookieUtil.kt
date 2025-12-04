package com.example.taskmapi.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class TokenToCookieUtil {
    fun toHeader(name: String, value: String?, durationMs: Long, response: HttpServletResponse){
        val cookie = Cookie(name, value).apply{
            isHttpOnly = true
            path = "/"
            secure = false
            if (value == null || durationMs <= 0){
                maxAge = 0
            } else {
                maxAge = (durationMs / 1000).toInt()
            }
            setAttribute("SameSite", "Lax")
        }

        response.addCookie(cookie)
    }

    fun clearCookies(response: HttpServletResponse) {
        toHeader("accessToken", null, 0, response)
        toHeader("refreshToken", null, 0, response)
    }
}