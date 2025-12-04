package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.entity.enums.Role
import com.example.taskmapi.exception.FieldTakenException
import com.example.taskmapi.exception.IdNotFoundException
import com.example.taskmapi.exception.UnauthorizedException
import com.example.taskmapi.exception.UserNotFoundException
import com.example.taskmapi.repository.UserAccountRepository
import com.example.taskmapi.utils.AuthEmailUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccRepo: UserAccountRepository,
    private val authEmail: AuthEmailUtil,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserAccountService::class.java)

    fun add(userAcc: UserAccountRequest): UserAccountResponse? {
        logger.debug("Creating account")
        if(userAccRepo.existsByEmail(userAcc.email)) throw FieldTakenException(userAcc.email)

        val hashedPassword = passwordEncoder.encode(userAcc.password)

        val newUser = UserAccountRequest(
            email = userAcc.email,
            password = hashedPassword
        )

        return userAccRepo.save(newUser.toEntity(Role.USER)).toResponse()
            .also{ logger.info("User ${it.id} created successfully") }
    }

    fun retrieve(pageable: Pageable): Page<UserAccountResponse> {
        logger.debug("Retrieving all accounts")
        return userAccRepo.findAll(pageable).map { it.toResponse() }.also{
            logger.info("Accounts retrieved successfully")
        }
    }

    fun retrieveById(id: Long, userEmail: String): UserAccountResponse? =
        userAccRepo.findByIdOrNull(id)?.apply {
            logger.debug("Retrieving account with $id ID")
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(id, this.email, user, "retrieve")
        }?.toResponse().also {
            logger.info("Account retrieved successfully")
        } ?: throw IdNotFoundException(id)

    fun update(id: Long, userAcc: AccountUpdateRequest, userEmail: String): UserAccountResponse =
        userAccRepo.findByIdOrNull(id)?.apply {
            logger.debug("Updating account with $id ID")
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(this.id, this.email, user, "update")
        }?.let{
            val newData = userAcc.toEntity()
            val hashedPass = passwordEncoder.encode(newData.password)

            val updated = it.copy(
                email = newData.email,
                password = hashedPass
            )
            userAccRepo.save(updated).toResponse()
        }.also {
            logger.info("Account updated successfully")
        } ?: throw IdNotFoundException(id)

    fun delete(id: Long, userEmail: String): Unit? =
        userAccRepo.findByIdOrNull(id)?.apply {
            logger.debug("Deleting account with $id ID")
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(id, this.email, user, "delete")
        }
            ?.let { userAccRepo.deleteById(id) }?.also { logger.info("Account deleted successfully")
        } ?: throw IdNotFoundException(id)
}