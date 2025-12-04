package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.service.UserProfileService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profile")
class UserProfileController(
    private val profileService: UserProfileService,
    private val authEmail: AuthEmailUtil
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody userProf: UserProfileRequest): UserProfileResponse {
        val email = authEmail.getUser()
        return profileService.create(userProf, email)
    }

    @GetMapping
    fun retrieve(pageable: Pageable): Page<UserProfileResponse> {
        return profileService.retrieve(pageable)
    }

    @GetMapping("/me")
    fun retrieveLoggedInUser(): UserProfileResponse? {
        val email = authEmail.getUser()
        return profileService.retrieveByUser(email)
    }

    @GetMapping("/{id}")
    fun retrieveById(@PathVariable id: Long): UserProfileResponse? {
        val email = authEmail.getUser()
        return profileService.retrieveById(id, email)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, profReq: UserProfileRequest): UserProfileResponse {
        val email = authEmail.getUser()
        return profileService.update(id, profReq, email)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Unit? {
        val email = authEmail.getUser()
        return profileService.delete(id, email)
    }
}