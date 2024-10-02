package com.xorker.draw.websocket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.xorker.draw.auth.token.TokenUseCase
import com.xorker.draw.exception.InvalidRequestValueException
import com.xorker.draw.exception.UnAuthenticationException
import com.xorker.draw.mafia.MafiaGameUseCase
import com.xorker.draw.support.logging.defaultApiJsonMap
import com.xorker.draw.support.logging.logger
import com.xorker.draw.support.logging.registerRequestId
import com.xorker.draw.user.User
import com.xorker.draw.user.UserId
import com.xorker.draw.websocket.message.request.RequestAction
import com.xorker.draw.websocket.message.request.WebSocketRequest
import com.xorker.draw.websocket.message.request.WebSocketRequestParser
import com.xorker.draw.websocket.message.request.mafia.SessionInitializeRequest
import com.xorker.draw.websocket.session.Session
import com.xorker.draw.websocket.session.SessionId
import com.xorker.draw.websocket.session.SessionManager
import com.xorker.draw.websocket.session.SessionWrapper
import org.slf4j.MDC
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

internal abstract class BaseWebSocketHandler(
    private val objectMapper: ObjectMapper,
    private val sessionManager: SessionManager,
    private val parser: WebSocketRequestParser,
    private val tokenUseCase: TokenUseCase,
    private val gameUseCase: MafiaGameUseCase,
) : TextWebSocketHandler() {
    private val logger = logger()

    abstract fun afterConnect(session: Session)
    abstract fun action(session: Session, request: WebSocketRequest)
    abstract fun afterDisconnect(session: Session?, status: CloseStatus)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        registerRequestId()
        val user = getUser(session) ?: throw UnAuthenticationException
        val locale = session.getHeader("locale") ?: throw InvalidRequestValueException

        val sessionDto = SessionWrapper(session, user, locale)
        setupMdc(sessionDto)
        sessionManager.registerSession(sessionDto)

        try {
            afterConnect(sessionDto)
        } finally {
            log(sessionDto.id, "WS_CONNECT")
            MDC.clear()
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        registerRequestId()

        val sessionId = SessionId(session.id)
        val sessionDto = sessionManager.getSession(sessionId) ?: return
        setupMdc(sessionDto)

        val request = parser.parse(message.payload)

        try {
            action(sessionDto, request)
        } finally {
            log(sessionId, request)
            MDC.clear()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        registerRequestId()

        val sessionId = SessionId(session.id)
        val sessionDto = sessionManager.unregisterSession(sessionId)
        if (sessionDto != null) {
            setupMdc(sessionDto)
        }

        try {
            afterDisconnect(sessionDto, status)
        } finally {
            log(
                sessionId,
                "WS_CLOSED",
                "status" to status,
            )
            MDC.clear()
        }
    }

    private fun setupMdc(session: Session) {
        val gameInfo = gameUseCase.getGameInfoByUserId(session.user.id) ?: return
        MDC.put("userId", session.user.id.value.toString())
        MDC.put("roomId", gameInfo.room.id.value)
    }

    private fun log(sessionId: SessionId, action: String, vararg additionalData: Pair<String, Any>) {
        val data = defaultApiJsonMap(
            "action" to action,
            "sessionId" to sessionId.value,
            "userId" to MDC.get("userId"),
            "roomId" to MDC.get("roomId"),
            *additionalData,
        )

        val log = objectMapper.writeValueAsString(data)
        logger.info(log)
    }

    private fun log(sessionId: SessionId, request: WebSocketRequest) {
        val body: Any? = if (request.action == RequestAction.INIT) {
            objectMapper.readValue(request.body, SessionInitializeRequest::class.java).copy(accessToken = "[masked]")
        } else {
            request.body
        }

        val data = defaultApiJsonMap(
            "action" to request.action,
            "requestBody" to body,
            "sessionId" to sessionId.value,
            "userId" to MDC.get("userId"),
            "roomId" to MDC.get("roomId"),
        )

        val log = objectMapper.writeValueAsString(data)
        logger.info(log)
    }

    private fun getUser(session: WebSocketSession): User? {
        val userId = getUserId(session) ?: return null
        val nickname = session.getHeader(HEADER_NICKNAME) ?: return null

        return User(userId, nickname)
    }

    private fun getUserId(session: WebSocketSession): UserId? {
        val header = session.getHeader(HEADER_AUTHORIZATION) ?: return null

        if (header.startsWith(HEADER_BEARER)) {
            val accessToken = header.substring(HEADER_BEARER.length)

            return tokenUseCase.getUserId(accessToken)
        }

        return null
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_NICKNAME = "Nickname"
        private const val HEADER_BEARER = "bearer "
    }
}
