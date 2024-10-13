package com.xorker.draw.mafia

import com.fasterxml.jackson.databind.ObjectMapper
import com.xorker.draw.mafia.entity.MafiaGameOptionRedisRepository
import com.xorker.draw.mafia.entity.MafiaPhaseRedisRepository
import com.xorker.draw.mafia.entity.MafiaRoomRedisRepository
import com.xorker.draw.mafia.entity.toDomain
import com.xorker.draw.mafia.entity.toEntity
import com.xorker.draw.room.Room
import com.xorker.draw.room.RoomId
import com.xorker.draw.room.RoomRepository
import com.xorker.draw.support.metric.MetricManager
import com.xorker.draw.timer.TimerRepository
import com.xorker.draw.user.UserId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
internal class MafiaGameAdapter(
    private val metricManager: MetricManager,
    private val gameOptionRedisRepository: MafiaGameOptionRedisRepository,
    private val phaseRedisRepository: MafiaPhaseRedisRepository,
    private val roomRedisRepository: MafiaRoomRedisRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val timerRepository: TimerRepository,
    private val objectMapper: ObjectMapper,
) : MafiaGameRepository, RoomRepository {

    override fun saveGameInfo(gameInfo: MafiaGameInfo) {
        val room = gameInfo.room
        if (room.isEmpty()) {
            removeGameInfo(gameInfo)
        } else {
            val roomId = gameInfo.room.id

            if (roomRedisRepository.existsById(roomId.value).not()) {
                metricManager.increaseGameCount()
            }

            gameOptionRedisRepository.save(gameInfo.gameOption.toEntity(roomId))
            phaseRedisRepository.save(gameInfo.phase.toEntity(roomId, objectMapper))
            roomRedisRepository.save(gameInfo.room.toEntity())

            room.players.forEach {
                redisTemplate
                    .opsForValue()
                    .set("Player:${it.userId.value}", room.id.value)
            }
        }
    }

    override fun removeGameInfo(gameInfo: MafiaGameInfo) {
        metricManager.decreaseGameCount()

        val room = gameInfo.room

        room.players
            .map { it.userId }
            .forEach { removePlayer(it) }

        val phase = gameInfo.phase

        if (phase is MafiaPhaseWithTimer) {
            timerRepository.cancelTimer(room.id)
        }

        gameOptionRedisRepository.deleteById(room.id.value)
        phaseRedisRepository.deleteById(room.id.value)
        roomRedisRepository.deleteById(room.id.value)
    }

    override fun getGameInfo(roomId: RoomId): MafiaGameInfo? {
        val room = roomRedisRepository.findByIdOrNull(roomId.value)?.toDomain() ?: return null
        val phase = phaseRedisRepository.findByIdOrNull(roomId.value)?.toDomain(objectMapper) ?: return null
        val gameOption = gameOptionRedisRepository.findByIdOrNull(roomId.value)?.toDomain() ?: return null

        return MafiaGameInfo(
            room = room,
            phase = phase,
            gameOption = gameOption,
        )
    }

    override fun getGameInfo(userId: UserId): MafiaGameInfo? {
        val roomId = redisTemplate
            .opsForValue()
            .get(userId.value.toString()) ?: return null

        return getGameInfo(RoomId(roomId))
    }

    override fun removePlayer(userId: UserId) {
        redisTemplate.delete(userId.value.toString())
    }

    override fun getRoom(roomId: RoomId): Room<MafiaPlayer>? {
        return roomRedisRepository.findByIdOrNull(roomId.value)?.toDomain() ?: return null
    }
}
