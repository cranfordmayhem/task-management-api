package com.example.taskmapi.service

import com.example.taskmapi.dto.*
import com.example.taskmapi.exception.*
import com.example.taskmapi.repository.*
import com.example.taskmapi.utils.AuthEmailUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val userProfileRepo: UserProfileRepository,
    private val authEmail: AuthEmailUtil
) {
    private val logger = LoggerFactory.getLogger(UserProfileService::class.java)

    fun create(userProf: UserProfileRequest, userEmail: String): UserProfileResponse {
        logger.debug("Creating account profile")
        val user = authEmail.checkUser(userEmail)
        return userProfileRepo.save(userProf.toEntity(user)).toResponse()
            .also { logger.info("Account profile created successfully") }
    }

    fun retrieveByUser(userEmail: String): UserProfileResponse? {
        logger.debug("Retrieving profile via logged in user")
        val user = authEmail.checkUser(userEmail)
        val profile = userProfileRepo.findByUserId(user.id)
            ?: throw IdNotFoundException(user.id)
        return profile.toResponse()
            .also { logger.info("Profile retrieved successfully") }
    }

    fun retrieve(pageable: Pageable): Page<UserProfileResponse> {
        logger.debug("Retrieving all Account Profiles")

        return userProfileRepo.findAll(pageable).map { it.toResponse() }
            .also { logger.info("Account profiles retrieved successfully") }
    }

    fun retrieveById(id: Long, userEmail: String): UserProfileResponse? =
        userProfileRepo.findByIdOrNull(id)?.apply {
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(id, this.user.email, user, "access")
        }?.toResponse().also { logger.info("Account profile retrieved successfully") }
            ?: throw IdNotFoundException(id)


    fun update(id: Long, profile: UserProfileRequest, userEmail: String): UserProfileResponse =
        userProfileRepo.findByIdOrNull(id)?.let{ existingProfile ->
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(id, existingProfile.user.email, user, "update" )

            val newProfileData = profile.toEntity(existingProfile.user)
            val updated = existingProfile.copy(
                firstName = newProfileData.firstName,
                lastName = newProfileData.lastName,
                age = newProfileData.age
            )

            userProfileRepo.save(updated).toResponse()
                .also{ logger.info("Account profile updated successfully") }
        } ?: throw IdNotFoundException(id)


    fun delete(id: Long, userEmail: String): Unit? =
        userProfileRepo.findByIdOrNull(id)?.apply{
            val user = authEmail.checkUser(userEmail)
            authEmail.verifyUser(id,this.user.email, user, "delete")
        }?.let { userProfileRepo.deleteById(id) }
            ?.also { logger.info("Account profile deleted successfully") }
}