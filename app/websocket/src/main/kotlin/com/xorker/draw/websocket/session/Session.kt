package com.xorker.draw.websocket.session

import com.xorker.draw.user.User
import java.time.LocalDateTime
import org.springframework.web.socket.WebSocketSession

@JvmInline
value class SessionId(val value: String)

interface Session {
    val origin: WebSocketSession
    val id: SessionId
    val user: User
    val locale: String
    var ping: LocalDateTime

    fun send(message: String)
}
