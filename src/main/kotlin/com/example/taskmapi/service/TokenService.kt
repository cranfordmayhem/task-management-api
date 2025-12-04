package com.example.taskmapi.service

import com.example.taskmapi.exception.InvalidTokenException
import com.example.taskmapi.repository.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SignatureException
import java.util.Base64
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Service
class TokenService(
    @Value("\${jwt.secret}") private val secret: String,
    private val refreshTokenRepository: RefreshTokenRepository
){
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }

    fun generateToken(
        subject: String,
        expiration: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String {
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expiration)
            .signWith(signingKey)
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenValid(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: ExpiredJwtException){
            throw InvalidTokenException("EXPIRED")
        } catch (ex: SignatureException){
            throw InvalidTokenException("INVALID_SIGNATURE")
        } catch (ex: MalformedJwtException) {
            throw InvalidTokenException("MALFORMED")
        } catch (ex: IllegalArgumentException) {
            throw InvalidTokenException("MISSING_OR_EMPTY")
        } catch (ex: Exception) {
            throw InvalidTokenException("INVALID")
        }
    }
}