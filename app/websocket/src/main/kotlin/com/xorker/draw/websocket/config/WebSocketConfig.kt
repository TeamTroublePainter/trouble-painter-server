package com.xorker.draw.websocket.config

import com.xorker.draw.websocket.MainWebSocketHandler
import com.xorker.draw.websocket.handler.RoomWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
internal class WebSocketConfig(
    private val handler: MainWebSocketHandler,
    private val roomWebSocketHandler: RoomWebSocketHandler,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(handler, "/trouble-painter")
            .addHandler(roomWebSocketHandler, "/mafia/room")
            .setAllowedOrigins("*")
    }
}
