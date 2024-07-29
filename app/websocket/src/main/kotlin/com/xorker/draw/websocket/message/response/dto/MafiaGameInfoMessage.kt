package com.xorker.draw.websocket.message.response.dto

import com.xorker.draw.mafia.MafiaGameOption
import com.xorker.draw.mafia.MafiaPlayer
import com.xorker.draw.user.UserId
import com.xorker.draw.websocket.ResponseAction
import com.xorker.draw.websocket.SessionMessage

data class MafiaGameInfoMessage(
    override val body: MafiaGameInfoBody,
) : SessionMessage {
    override val action: ResponseAction = ResponseAction.GAME_INFO
    override val status: SessionMessage.Status = SessionMessage.Status.OK
}

data class MafiaGameInfoBody(
    val userId: UserId,
    val turn: Int,
    val isMafia: Boolean = false,
    val turnList: List<MafiaPlayer>,
    val category: String,
    val answer: String,
    val gameOption: MafiaGameOptionResponse,
)

data class MafiaGameOptionResponse(
    val minimum: Int,
    val maximum: Int,
    val readyTime: Long,
    val animationTime: Long,
    val round: Int,
    val turnTime: Long,
    val turnCount: Int,
    val voteTime: Long,
    val answerTime: Long,
)

fun MafiaGameOption.toResponse(): MafiaGameOptionResponse = MafiaGameOptionResponse(
    minimum = minimum,
    maximum = maximum,
    readyTime = readyTime.toSeconds(),
    animationTime = animationTime.toSeconds(),
    round = round,
    turnTime = turnTime.toSeconds(),
    turnCount = turnCount,
    voteTime = voteTime.toSeconds(),
    answerTime = answerTime.toSeconds(),
)
