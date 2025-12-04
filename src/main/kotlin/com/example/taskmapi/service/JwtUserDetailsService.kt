package com.example.taskmapi.service

import com.example.taskmapi.exception.UserNotFoundException
import com.example.taskmapi.repository.UserAccountRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    private val userAccountRepo: UserAccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userAccountRepo.findByEmail(username)?.let { user ->
            User.builder()
                .username(user.email)
                .password(user.password)
                .roles(user.role.name) // ROLE_ prefix automatically added
                .build()
        } ?: throw UserNotFoundException(username)
}