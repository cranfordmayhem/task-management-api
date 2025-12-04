package com.example.taskmapi.controller

import com.example.taskmapi.dto.AccountUpdateRequest
import com.example.taskmapi.dto.UserAccountRequest
import com.example.taskmapi.dto.UserAccountResponse
import com.example.taskmapi.service.UserAccountService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/account")
class UserAccountController(
    private val userAccService: UserAccountService,
    private val authEmail: AuthEmailUtil
) {

    @GetMapping("/{id}")
    fun retrieveById(@PathVariable id: Long): UserAccountResponse? {
        val email = authEmail.getUser()
        return userAccService.retrieveById(id, email)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody userAcc: AccountUpdateRequest):
            UserAccountResponse {
        val email = authEmail.getUser()
        return userAccService.update(id, userAcc, email)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Unit? {
        val email = authEmail.getUser()
        return userAccService.delete(id, email)
    }
}