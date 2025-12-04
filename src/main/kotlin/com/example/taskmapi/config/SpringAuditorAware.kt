package com.example.taskmapi.config

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Optional

@Component("springAuditorAware")
class SpringAuditorAware: AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        val authentication = SecurityContextHolder.getContext().authentication?.name ?: "system"
        return Optional.of(authentication)
    }
}