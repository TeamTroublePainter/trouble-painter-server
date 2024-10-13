package com.xorker.draw.mafia.entity

import com.xorker.draw.mafia.MafiaGameOption
import com.xorker.draw.room.RoomId
import java.time.Duration
import org.springframework.data.annotation.Id

internal data class MafiaGameOptionRedisEntity(
    @Id val id: String,
    val minimum: Int,
    val maximum: Int,
    val readyTime: Long,
    val introAnimationTime: Long,
    val roundAnimationTime: Long,
    val round: Int,
    val turnTime: Long,
    val turnCount: Int,
    val voteTime: Long,
    val answerTime: Long,
    val endTime: Long,
)

internal fun MafiaGameOption.toEntity(roomId: RoomId): MafiaGameOptionRedisEntity = MafiaGameOptionRedisEntity(
    id = roomId.value,
    minimum = this.minimum,
    maximum = this.maximum,
    readyTime = this.readyTime.toMillis(),
    introAnimationTime = this.introAnimationTime.toMillis(),
    roundAnimationTime = this.roundAnimationTime.toMillis(),
    round = this.round,
    turnTime = this.turnTime.toMillis(),
    turnCount = this.turnCount,
    voteTime = this.voteTime.toMillis(),
    answerTime = this.answerTime.toMillis(),
    endTime = this.endTime.toMillis(),
)

internal fun MafiaGameOptionRedisEntity.toDomain(): MafiaGameOption = MafiaGameOption(
    minimum = this.minimum,
    maximum = this.maximum,
    readyTime = Duration.ofMillis(this.readyTime),
    introAnimationTime = Duration.ofMillis(this.introAnimationTime),
    roundAnimationTime = Duration.ofMillis(this.roundAnimationTime),
    round = this.round,
    turnTime = Duration.ofMillis(this.turnTime),
    turnCount = this.turnCount,
    voteTime = Duration.ofMillis(this.voteTime),
    answerTime = Duration.ofMillis(this.answerTime),
    endTime = Duration.ofMillis(this.endTime),
)
