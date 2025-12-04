package com.example.taskmapi.service

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.SocketIOClient
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy

@Service
class SocketIOService(
    private val server: SocketIOServer,
    private val tokenService: TokenService,
    private val jwtUserDetailsService: JwtUserDetailsService
) {

    @PostConstruct
    fun startServer() {
        server.addConnectListener { client -> handleConnect(client) }
        server.start()
        println("Socket.IO server started on port ${server.configuration.port}")
    }

    @PreDestroy
    fun stopServer() {
        server.stop()
    }

    private fun handleConnect(client: SocketIOClient) {
        val token = client.handshakeData.getSingleUrlParam("token")
        if (token.isNullOrBlank()) {
            client.disconnect()
            return
        }

        try {
            if (!tokenService.isTokenValid(token)) {
                client.disconnect()
                return
            }
        } catch (ex: Exception) {
            client.disconnect()
            return
        }

        val username = tokenService.extractUsername(token)

        // Verify user exists
        try {
            jwtUserDetailsService.loadUserByUsername(username)
        } catch (ex: Exception) {
            client.disconnect()
            return
        }

        // Join board room if boardId is provided
        val boardId = client.handshakeData.urlParams["boardId"]?.firstOrNull()?.toLong()
        if (boardId != null) {
            client.joinRoom("board:$boardId")
        }
    }

    fun emitTasksUpdate(boardId: Long, tasks: Any) {
        server.getRoomOperations("board:$boardId").sendEvent("tasks:update", tasks)
    }
}
