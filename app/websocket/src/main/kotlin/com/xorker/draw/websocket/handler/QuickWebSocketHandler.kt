package com.xorker.draw.websocket.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.xorker.draw.auth.token.TokenUseCase
import com.xorker.draw.mafia.MafiaGameUseCase
import com.xorker.draw.mafia.UserConnectionUseCase
import com.xorker.draw.mafia.WaitingQueueUseCase
import com.xorker.draw.websocket.WebSocketRouter
import com.xorker.draw.websocket.message.request.WebSocketRequest
import com.xorker.draw.websocket.message.request.WebSocketRequestParser
import com.xorker.draw.websocket.session.Session
import com.xorker.draw.websocket.session.SessionManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus

@Component
internal class QuickWebSocketHandler(
    objectMapper: ObjectMapper,
    sessionManager: SessionManager,
    parser: WebSocketRequestParser,
    tokenUseCase: TokenUseCase,
    gameUseCase: MafiaGameUseCase,
    private val userConnectionUseCase: UserConnectionUseCase,
    private val router: WebSocketRouter,
    private val waitingQueueUseCase: WaitingQueueUseCase,
) : BaseWebSocketHandler(
    objectMapper,
    sessionManager,
    parser,
    tokenUseCase,
    gameUseCase,
) {
    override fun afterConnect(session: Session) {
        waitingQueueUseCase.enqueue(session.user, session.locale)
    }

    override fun action(session: Session, request: WebSocketRequest) {
        router.route(session, request)
    }

    override fun afterDisconnect(session: Session?, status: CloseStatus) {
        val user = session?.user ?: return

        waitingQueueUseCase.remove(user, session.locale)

        when (status) {
            CloseStatus.NORMAL -> userConnectionUseCase.exitUser(user)
            else -> userConnectionUseCase.disconnectUser(user)
        }
    }
}
