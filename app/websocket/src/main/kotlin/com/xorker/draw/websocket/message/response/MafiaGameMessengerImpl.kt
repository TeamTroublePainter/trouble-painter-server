package com.xorker.draw.websocket.message.response

import com.xorker.draw.exception.InvalidMafiaGamePlayingPhaseStatusException
import com.xorker.draw.mafia.MafiaGameInfo
import com.xorker.draw.mafia.MafiaGameMessenger
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.MafiaPhaseWithTurn
import com.xorker.draw.room.RoomId
import com.xorker.draw.user.UserId
import com.xorker.draw.websocket.BranchedBroadcastEvent
import com.xorker.draw.websocket.BroadcastEvent
import com.xorker.draw.websocket.RespectiveBroadcastEvent
import com.xorker.draw.websocket.SessionMessage
import com.xorker.draw.websocket.broker.WebSocketBroadcaster
import com.xorker.draw.websocket.message.response.dto.MafiaGameDrawBody
import com.xorker.draw.websocket.message.response.dto.MafiaGameDrawMessage
import com.xorker.draw.websocket.message.response.dto.MafiaGameInfoBody
import com.xorker.draw.websocket.message.response.dto.MafiaGameInfoMessage
import com.xorker.draw.websocket.message.response.dto.MafiaGameReadyBody
import com.xorker.draw.websocket.message.response.dto.MafiaGameReadyMessage
import com.xorker.draw.websocket.message.response.dto.MafiaPlayerListBody
import com.xorker.draw.websocket.message.response.dto.MafiaPlayerListMessage
import com.xorker.draw.websocket.message.response.dto.MafiaPlayerTurnListBody
import com.xorker.draw.websocket.message.response.dto.MafiaPlayerTurnListMessage
import com.xorker.draw.websocket.message.response.dto.toResponse
import java.time.LocalDateTime
import org.springframework.stereotype.Component

@Component
class MafiaGameMessengerImpl(
    private val broadcaster: WebSocketBroadcaster,
) : MafiaGameMessenger {

    override fun broadcastPlayerList(gameInfo: MafiaGameInfo) {
        val roomId = gameInfo.room.id
        val phase = gameInfo.phase

        val list =
            if (phase is MafiaPhaseWithTurn) {
                phase.turnList
            } else {
                gameInfo.room.players
            }

        val message = MafiaPlayerListMessage(
            MafiaPlayerListBody(
                list.map { it.toResponse(gameInfo.room.owner) }.toList(),
            ),
        )

        val event = BroadcastEvent(roomId, message)

        broadcaster.publishBroadcastEvent(event)
    }

    override fun broadcastGameInfo(mafiaGameInfo: MafiaGameInfo) {
        val roomId = mafiaGameInfo.room.id

        val phase = mafiaGameInfo.phase as? MafiaPhase.Playing ?: throw InvalidMafiaGamePlayingPhaseStatusException

        val mafia = phase.mafiaPlayer
        val keyword = phase.keyword

        val gameOption = mafiaGameInfo.gameOption

        val message = MafiaGameInfoMessage(
            MafiaGameInfoBody(
                category = keyword.category,
                answer = keyword.answer,
                gameOption = gameOption.toResponse(),
            ),
        )

        val branchedMessage = MafiaGameInfoMessage(
            MafiaGameInfoBody(
                isMafia = true,
                category = keyword.category,
                answer = keyword.answer,
                gameOption = gameOption.toResponse(),
            ),
        )

        val branched = setOf(mafia.userId)

        val event = BranchedBroadcastEvent(
            roomId = roomId,
            branched = branched,
            message = message,
            branchedMessage = branchedMessage,
        )

        broadcaster.publishBranchedBroadcastEvent(event)
    }

    override fun broadcastGameReady(mafiaGameInfo: MafiaGameInfo) {
        val roomId = mafiaGameInfo.room.id

        val phase = mafiaGameInfo.phase as? MafiaPhase.Playing ?: throw InvalidMafiaGamePlayingPhaseStatusException

        val turnList = phase.turnList

        val messages = mutableMapOf<UserId, SessionMessage>()

        turnList.forEachIndexed { i, player ->
            val message = MafiaGameReadyMessage(
                MafiaGameReadyBody(
                    turn = i + 1,
                    player = player.toResponse(mafiaGameInfo.room.owner),
                ),
            )
            messages[player.userId] = message
        }

        val event = RespectiveBroadcastEvent(
            roomId = roomId,
            messages = messages,
        )

        broadcaster.publishRespectiveBroadcastEvent(event)
    }

    override fun broadcastPlayerTurnList(mafiaGameInfo: MafiaGameInfo) {
        val room = mafiaGameInfo.room
        val roomId = room.id

        val phase = mafiaGameInfo.phase as? MafiaPhase.Playing ?: throw InvalidMafiaGamePlayingPhaseStatusException
        val turn = phase.turn
        val turnList = phase.turnList

        val currentTurnPlayer = turnList[turn]

        val mafiaPlayerResponses = turnList
            .map {
                it.toResponse(mafiaGameInfo.room.owner)
            }.toList()

        val message = MafiaPlayerTurnListMessage(
            MafiaPlayerTurnListBody(
                turn = turn,
                players = mafiaPlayerResponses,
            ),
        )

        val branchedMessage = MafiaPlayerTurnListMessage(
            MafiaPlayerTurnListBody(
                turn = turn,
                isMyTurn = true,
                players = mafiaPlayerResponses,
            ),
        )

        val branched = setOf(currentTurnPlayer.userId)

        val event = BranchedBroadcastEvent(
            roomId = roomId,
            branched = branched,
            message = message,
            branchedMessage = branchedMessage,
        )

        broadcaster.publishBranchedBroadcastEvent(event)
    }

    override fun broadcastDraw(roomId: RoomId, data: Map<String, Any>) {
        val message = MafiaGameDrawMessage(data)
        broadcaster.broadcast(roomId, message)
    }
}
