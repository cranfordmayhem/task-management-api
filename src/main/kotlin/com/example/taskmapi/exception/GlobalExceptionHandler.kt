package com.example.taskmapi.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    object ErrorUtil {
        fun errorMapper(status: Int, error: String, request: HttpServletRequest): Map<String, Any> {
            return mapOf(
                "timestamp" to LocalDateTime.now(),
                "status" to status,
                "error" to error,
                "path" to request.requestURI
            )
        }
        fun errorHandler(error: String){
            val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
            return logger.debug("Error: $error")
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(handler: UserNotFoundException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.BAD_REQUEST.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(handler: InvalidTokenException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.UNAUTHORIZED.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationUserException::class)
    fun handleAuthUserException(handler: AuthenticationUserException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.UNAUTHORIZED.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FieldTakenException::class)
    fun handleFieldTakenException(handler: FieldTakenException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.BAD_REQUEST.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IdNotFoundException::class)
    fun handleIdNotFoundException(handler: IdNotFoundException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.NOT_FOUND.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(handler: UnauthorizedException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.NOT_FOUND.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException::class)
    fun handleUnauthenticated(handler: UnauthenticatedException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.UNAUTHORIZED.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MemberNotFoundException::class)
    fun handleMemberNotFoundException(handler: MemberNotFoundException, request: HttpServletRequest):
            Map<String, Any> {
        val error = handler.message.toString()

        return ErrorUtil.errorMapper(HttpStatus.NOT_FOUND.value(), error, request)
            .also { ErrorUtil.errorHandler(error) }
    }
}

class UserNotFoundException(email: String): RuntimeException("User $email not found")
class InvalidTokenException(message: String): RuntimeException(message)
class AuthenticationUserException(): RuntimeException("Username or Password is invalid")
class FieldTakenException(content: Any): RuntimeException("$content field is already taken")
class IdNotFoundException(id: Long): RuntimeException("ID $id not found")
class UnauthorizedException(action: String, id: Long): RuntimeException("Not Authorized to $action $id")
class UnauthenticatedException(): RuntimeException("User not authenticated")
class MemberNotFoundException(): RuntimeException("Member not found")