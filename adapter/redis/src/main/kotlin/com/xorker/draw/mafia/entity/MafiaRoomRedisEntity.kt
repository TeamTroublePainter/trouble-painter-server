package com.xorker.draw.mafia.entity

import com.xorker.draw.mafia.MafiaPlayer
import com.xorker.draw.room.Room
import com.xorker.draw.room.RoomId
import com.xorker.draw.user.UserId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("MafiaGameRoom")
internal data class MafiaRoomRedisEntity(
    @Id val id: String,
    val locale: String,
    val ownerId: Long,
    val maxMemberNum: Int,
    val players: List<MafiaPlayerRedisEntity>,
    val isRandomMatching: Boolean,
)

internal data class MafiaPlayerRedisEntity(
    val id: Long,
    val nickname: String,
    val color: String,
    val isConnect: Boolean,
)

internal fun Room<MafiaPlayer>.toEntity(): MafiaRoomRedisEntity = MafiaRoomRedisEntity(
    id = this.id.value,
    locale = this.locale,
    ownerId = this.owner.userId.value,
    maxMemberNum = this.maxMemberNum,
    players = this.players.map { it.toEntity() },
    isRandomMatching = this.isRandomMatching,
)

internal fun MafiaRoomRedisEntity.toDomain(): Room<MafiaPlayer> {
    val players = this.players.map { it.toDomain() }

    return Room<MafiaPlayer>(
        id = RoomId(this.id),
        locale = this.locale,
        owner = players.first { it.userId.value == this.ownerId },
        maxMemberNum = this.maxMemberNum,
        players = players.toMutableList(),
        isRandomMatching = isRandomMatching,
    )
}

internal fun MafiaPlayer.toEntity(): MafiaPlayerRedisEntity = MafiaPlayerRedisEntity(
    id = this.userId.value,
    nickname = this.nickname,
    color = this.color,
    isConnect = this.isConnect,
)

internal fun MafiaPlayerRedisEntity.toDomain(): MafiaPlayer = MafiaPlayer(
    userId = UserId(this.id),
    nickname = this.nickname,
    color = this.color,
    isConnect = this.isConnect,
)
