package com.xorker.draw.websocket.session

import com.xorker.draw.user.User
import java.time.LocalDateTime
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

internal class SessionWrapper(
    override val origin: WebSocketSession,
    override val user: User,
    override val locale: String,
) : Session {
    override val id: SessionId = SessionId(origin.id)
    override var ping: LocalDateTime = LocalDateTime.now()

    override fun send(message: String) {
        synchronized(origin) {
            origin.sendMessage(TextMessage(message))
        }
    }
}
