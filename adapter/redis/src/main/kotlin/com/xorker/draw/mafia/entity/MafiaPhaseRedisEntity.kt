package com.xorker.draw.mafia.entity

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.xorker.draw.mafia.MafiaKeyword
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.turn.TurnInfo
import com.xorker.draw.room.RoomId
import com.xorker.draw.user.UserId
import java.util.Vector
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("MafiaPhase")
internal data class MafiaPhaseRedisEntity(
    @Id val id: String,
    val status: MafiaPhaseRedisStatus,
    val turnList: List<MafiaPlayerRedisEntity>? = null,
    val mafiaPlayerId: Long? = null,
    val keyword: MafiaKeywordRedisEntity? = null,
    val drawData: String? = null,
    val voteInfo: Map<Long, List<Long>>? = null,
    val round: Int? = null,
    val turn: Int? = null,
    val answer: String? = null,
    val showAnswer: Boolean? = null,
    val isMafiaWin: Boolean? = null,
)

internal data class MafiaKeywordRedisEntity(
    val category: String,
    val answer: String,
)

internal fun MafiaKeyword.toEntity(): MafiaKeywordRedisEntity = MafiaKeywordRedisEntity(
    answer = this.answer,
    category = this.category,
)

internal fun MafiaKeywordRedisEntity.toDomain(): MafiaKeyword = MafiaKeyword(
    answer = this.answer,
    category = this.category,
)

internal enum class MafiaPhaseRedisStatus {
    WAIT,
    READY,
    PLAYING,
    VOTE,
    INFER_ANSWER,
    END,
}

internal fun MafiaPhase.toEntity(roomId: RoomId, objectMapper: ObjectMapper): MafiaPhaseRedisEntity {
    return when (this) {
        is MafiaPhase.Wait -> this.toEntity(roomId)
        is MafiaPhase.Ready -> this.toEntity(roomId)
        is MafiaPhase.Playing -> this.toEntity(roomId, objectMapper)
        is MafiaPhase.Vote -> this.toEntity(roomId, objectMapper)
        is MafiaPhase.InferAnswer -> this.toEntity(roomId, objectMapper)
        is MafiaPhase.End -> this.toEntity(roomId, objectMapper)
    }
}

internal fun MafiaPhaseRedisEntity.toDomain(objectMapper: ObjectMapper): MafiaPhase {
    return when (this.status) {
        MafiaPhaseRedisStatus.WAIT -> MafiaPhase.Wait
        MafiaPhaseRedisStatus.READY -> this.toReady()
        MafiaPhaseRedisStatus.PLAYING -> this.toPlaying(objectMapper)
        MafiaPhaseRedisStatus.VOTE -> this.toPlaying(objectMapper)
        MafiaPhaseRedisStatus.INFER_ANSWER -> this.toInferAnswer(objectMapper)
        MafiaPhaseRedisStatus.END -> this.toEnd(objectMapper)
    }
}

private fun MafiaPhase.Wait.toEntity(roomId: RoomId): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.WAIT,
)

private fun MafiaPhaseRedisEntity.toReady(): MafiaPhase.Ready {
    val players = turnList!!.map { it.toDomain() }
    return MafiaPhase.Ready(
        turnList = players,
        mafiaPlayer = players.first { it.userId.value == this.mafiaPlayerId },
        keyword = this.keyword!!.toDomain(),
    )
}

private fun MafiaPhase.Ready.toEntity(roomId: RoomId): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.READY,
    turnList = this.turnList.map { it.toEntity() },
    mafiaPlayerId = this.mafiaPlayer.userId.value,
    keyword = this.keyword.toEntity(),
)

private fun MafiaPhaseRedisEntity.toPlaying(objectMapper: ObjectMapper): MafiaPhase.Playing {
    val players = turnList!!.map { it.toDomain() }

    return MafiaPhase.Playing(
        turnList = players,
        mafiaPlayer = players.first { it.userId.value == this.mafiaPlayerId },
        keyword = this.keyword!!.toDomain(),
        turnInfo = TurnInfo(this.round!!, this.turn!!),
        drawData = objectMapper.readValue(this.drawData!!),
    )
}

private fun MafiaPhase.Playing.toEntity(roomId: RoomId, objectMapper: ObjectMapper): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.PLAYING,
    turnList = this.turnList.map { it.toEntity() },
    mafiaPlayerId = this.mafiaPlayer.userId.value,
    keyword = this.keyword.toEntity(),
    round = this.turnInfo.round,
    turn = this.turnInfo.turn,
    drawData = objectMapper.writeValueAsString(this.drawData),
)

private fun MafiaPhaseRedisEntity.toVote(objectMapper: ObjectMapper): MafiaPhase.Vote {
    val players = turnList!!.map { it.toDomain() }

    return MafiaPhase.Vote(
        turnList = players,
        mafiaPlayer = players.first { it.userId.value == this.mafiaPlayerId },
        keyword = this.keyword!!.toDomain(),
        drawData = objectMapper.readValue(this.drawData!!),
        players = voteInfo!!.map { entry -> UserId(entry.key) to Vector(entry.value.map { UserId(it) }) }.toMap(),
    )
}

private fun MafiaPhase.Vote.toEntity(roomId: RoomId, objectMapper: ObjectMapper): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.VOTE,
    turnList = this.turnList.map { it.toEntity() },
    mafiaPlayerId = this.mafiaPlayer.userId.value,
    keyword = this.keyword.toEntity(),
    drawData = objectMapper.writeValueAsString(this.drawData),
    voteInfo = players.map { entry -> entry.key.value to entry.value.map { it.value }.toList() }.toMap(),
)

private fun MafiaPhaseRedisEntity.toInferAnswer(objectMapper: ObjectMapper): MafiaPhase.InferAnswer {
    val players = turnList!!.map { it.toDomain() }

    return MafiaPhase.InferAnswer(
        turnList = players,
        mafiaPlayer = players.first { it.userId.value == this.mafiaPlayerId },
        keyword = this.keyword!!.toDomain(),
        drawData = objectMapper.readValue(this.drawData!!),
        answer = this.answer,
    )
}

private fun MafiaPhase.InferAnswer.toEntity(roomId: RoomId, objectMapper: ObjectMapper): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.INFER_ANSWER,
    turnList = this.turnList.map { it.toEntity() },
    mafiaPlayerId = this.mafiaPlayer.userId.value,
    keyword = this.keyword.toEntity(),
    drawData = objectMapper.writeValueAsString(this.drawData),
    answer = this.answer,
)

private fun MafiaPhaseRedisEntity.toEnd(objectMapper: ObjectMapper): MafiaPhase.End {
    val players = turnList!!.map { it.toDomain() }

    return MafiaPhase.End(
        turnList = players,
        mafiaPlayer = players.first { it.userId.value == this.mafiaPlayerId },
        keyword = this.keyword!!.toDomain(),
        drawData = objectMapper.readValue(this.drawData!!),
        answer = this.answer,
        isMafiaWin = this.isMafiaWin!!,
        showAnswer = this.showAnswer!!,
    )
}

private fun MafiaPhase.End.toEntity(roomId: RoomId, objectMapper: ObjectMapper): MafiaPhaseRedisEntity = MafiaPhaseRedisEntity(
    id = roomId.value,
    status = MafiaPhaseRedisStatus.END,
    turnList = this.turnList.map { it.toEntity() },
    mafiaPlayerId = this.mafiaPlayer.userId.value,
    keyword = this.keyword.toEntity(),
    drawData = objectMapper.writeValueAsString(this.drawData),
    answer = this.answer,
    isMafiaWin = this.isMafiaWin,
    showAnswer = this.showAnswer,
)
