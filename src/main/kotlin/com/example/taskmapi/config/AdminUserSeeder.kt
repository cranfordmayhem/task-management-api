package com.example.taskmapi.config

import com.example.taskmapi.entity.enums.*
import com.example.taskmapi.entity.UserAccount
import com.example.taskmapi.repository.UserAccountRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AdminUserSeeder(val userAccountRepo: UserAccountRepository, val passwordEncoder: PasswordEncoder){

    private val logger = LoggerFactory.getLogger(AdminUserSeeder::class.java)

    @Bean
    fun seedAdminUser() = CommandLineRunner {

        if (!userAccountRepo.existsByRole(Role.ADMIN)) {
            val adminUser = UserAccount(
                email = "admin@email.com",
                password = passwordEncoder.encode("adminPass"),
                role = Role.ADMIN
            )
            userAccountRepo.save(adminUser)
            logger.debug("Default admin created")
        } else {
            logger.debug("Admin user already exists")
        }
    }
}