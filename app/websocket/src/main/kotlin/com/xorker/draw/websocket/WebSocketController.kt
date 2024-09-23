package com.xorker.draw.websocket

import com.xorker.draw.exception.AlreadyPlayingRoomException
import com.xorker.draw.exception.InvalidRequestOtherPlayingException
import com.xorker.draw.exception.MaxRoomException
import com.xorker.draw.exception.NotFoundRoomException
import com.xorker.draw.mafia.MafiaGameUseCase
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.event.MafiaGameRandomMatchingEvent
import com.xorker.draw.mafia.phase.MafiaPhaseUseCase
import com.xorker.draw.room.RoomId
import com.xorker.draw.room.RoomRepository
import com.xorker.draw.websocket.message.request.mafia.MafiaGameRandomMatchingRequest
import com.xorker.draw.websocket.message.request.mafia.SessionInitializeRequest
import com.xorker.draw.websocket.session.SessionFactory
import com.xorker.draw.websocket.session.SessionManager
import org.slf4j.MDC
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
internal class WebSocketController(
    private val sessionFactory: SessionFactory,
    private val waitingQueueUseCase: WaitingQueueUseCase,
    private val sessionEventListener: List<SessionEventListener>,
    private val sessionManager: SessionManager,
    private val roomRepository: RoomRepository,
    private val mafiaGameUseCase: MafiaGameUseCase,
    private val mafiaPhaseUseCase: MafiaPhaseUseCase,
) {

    fun initializeWaitingQueueSession(session: WebSocketSession, request: MafiaGameRandomMatchingRequest) {
        val sessionDto = sessionFactory.create(session, request)

        sessionManager.registerSession(sessionDto)
        waitingQueueUseCase.enqueue(sessionDto.user, sessionDto.locale)
    }

    fun initializeSession(session: WebSocketSession, request: SessionInitializeRequest) {
        val sessionDto = sessionFactory.create(session, request)

        val joinedRoomId = mafiaGameUseCase.getGameInfoByUserId(sessionDto.user.id)?.room?.id
        if (joinedRoomId != null && request.roomId != joinedRoomId.value) {
            throw InvalidRequestOtherPlayingException
        }

        val roomId = RoomId(request.roomId?.uppercase() ?: generateRoomId())
        // TODO 여기가맞나?
        MDC.put("roomId", roomId.value)

        if (request.roomId == null) {
            sessionManager.registerSession(sessionDto)
            sessionEventListener.forEach {
                it.connectSession(sessionDto.user.id, roomId, request.nickname, request.locale)
            }
            return
        }

        val gameInfo = mafiaGameUseCase.getGameInfoByRoomId(roomId) ?: throw NotFoundRoomException

        synchronized(gameInfo) {
            if (gameInfo.phase != MafiaPhase.Wait && gameInfo.room.players.any { it.userId == sessionDto.user.id }.not()) {
                throw AlreadyPlayingRoomException
            }

            if (gameInfo.gameOption.maximum <= gameInfo.room.size()) {
                throw MaxRoomException
            }

            sessionManager.registerSession(sessionDto)
            sessionEventListener.forEach {
                it.connectSession(sessionDto.user.id, roomId, request.nickname, request.locale)
            }
        }
    }

    @EventListener
    fun initializeSession(event: MafiaGameRandomMatchingEvent) {
        val players = event.players

        val roomId = RoomId(generateRoomId())

        players.forEach { user ->
            sessionEventListener.forEach { eventListener ->
                eventListener.connectSession(user, roomId, event.locale)
            }
        }

        mafiaPhaseUseCase.startGame(roomId)
    }

    private fun generateRoomId(): String {
        var value: String

        do {
            val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            value = (1..6)
                .map { charset.random() }
                .joinToString("")
        } while (roomRepository.getRoom(RoomId(value)) != null)

        return value
    }
}
