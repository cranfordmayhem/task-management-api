package com.example.taskmapi.config

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class SocketIOConfig {

    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = Configuration()
        config.port = 9092 // Choose a port for Socket.IO server
        config.origin = "*"
        return SocketIOServer(config)
    }
}
