package com.xorker.draw.mafia.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.xorker.draw.mafia.MafiaGameInfo

data class MafiaGameInfoRedisEntity @JsonCreator constructor(
    @JsonProperty("room") val room: MafiaRoomRedisEntity,
    @JsonProperty("phase") val phase: MafiaPhaseRedisEntity,
    @JsonProperty("gameOption") val gameOption: MafiaGameOptionRedisEntity,
) {
    fun toDomain(): MafiaGameInfo = MafiaGameInfo(
        room = room.toDomain(),
        phase = phase.toDomain(),
        gameOption = gameOption.toDomain(),
    )
}

fun MafiaGameInfo.toMafiaGameInfoRedisEntity(): MafiaGameInfoRedisEntity = MafiaGameInfoRedisEntity(
    room = MafiaRoomRedisEntity(
        id = room.id.value,
        locale = room.locale,
        owner = MafiaPlayerRedisEntity(
            id = room.owner.userId.value,
            nickname = room.owner.nickname,
            color = room.owner.color,
            isConnect = room.owner.isConnect(),
        ),
        maxMemberNum = room.maxMemberNum,
        players = room.players.map { player ->
            MafiaPlayerRedisEntity(
                id = player.userId.value,
                nickname = player.nickname,
                color = player.color,
                isConnect = player.isConnect(),
            )
        },
        isRandomMatching = room.isRandomMatching,
    ),
    phase = phase.toMafiaPhaseRedisEntity(),
    gameOption = MafiaGameOptionRedisEntity(
        minimum = gameOption.minimum,
        maximum = gameOption.maximum,
        readyTime = gameOption.readyTime.toMillis(),
        introAnimationTime = gameOption.introAnimationTime.toMillis(),
        roundAnimationTime = gameOption.roundAnimationTime.toMillis(),
        round = gameOption.round,
        turnTime = gameOption.turnTime.toMillis(),
        turnCount = gameOption.turnCount,
        voteTime = gameOption.voteTime.toMillis(),
        answerTime = gameOption.answerTime.toMillis(),
        endTime = gameOption.endTime.toMillis(),
    ),
)
