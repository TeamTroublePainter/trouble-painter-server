package com.xorker.draw.websocket.handler

import org.springframework.web.socket.WebSocketSession

fun WebSocketSession.getHeader(key: String): String? {
    val headers = this.handshakeHeaders[key] ?: return null
    if (headers.isEmpty()) return null
    return headers[0]
}
