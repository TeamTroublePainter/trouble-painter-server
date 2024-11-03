package com.xorker.draw.mafia.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.turn.TurnInfo
import com.xorker.draw.user.UserId
import java.util.*

data class MafiaPhaseRedisEntity @JsonCreator constructor(
    @JsonProperty("status") val status: RedisMafiaPhaseStatus,
    @JsonProperty("turnList") val turnList: List<MafiaPlayerRedisEntity>? = null,
    @JsonProperty("mafiaPlayer") val mafiaPlayer: MafiaPlayerRedisEntity? = null,
    @JsonProperty("keyword") val keyword: MafiaKeywordRedisEntity? = null,
    @JsonProperty("drawData") val drawData: List<DrawInfoRedisEntity>? = null,
    @JsonProperty("players") val players: Map<Long, List<Long>>? = null,
    @JsonProperty("round") val round: Int? = null,
    @JsonProperty("turn") val turn: Int? = null,
    @JsonProperty("answer") val answer: String? = null,
    @JsonProperty("showAnswer") val showAnswer: Boolean? = null,
    @JsonProperty("mafiaWin") val isMafiaWin: Boolean? = null,
)

data class DrawInfoRedisEntity @JsonCreator constructor(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("draw") val draw: Map<String, Any>,
)

enum class RedisMafiaPhaseStatus {
    WAIT,
    READY,
    PLAYING,
    VOTE,
    INFER_ANSWER,
    END,
    ;
}

fun serializePlayers(players: Map<UserId, Vector<UserId>>): Map<Long, List<Long>> {
    val serializedPlayers = mutableMapOf<Long, MutableList<Long>>()
    players.forEach { player ->
        serializedPlayers[player.key.value] = mutableListOf()
        players[player.key]?.forEach { userId ->
            serializedPlayers[player.key.value]?.add(userId.value)
        }
    }
    return serializedPlayers
}

fun deserializePlayers(players: Map<Long, List<Long>>): Map<UserId, Vector<UserId>> {
    val deserializedPlayers = mutableMapOf<UserId, Vector<UserId>>()
    players.forEach { player ->
        deserializedPlayers[UserId(player.key)] = Vector()
        players[player.key]?.forEach { userId ->
            deserializedPlayers[UserId(player.key)]?.add(UserId(userId))
        }
    }
    return deserializedPlayers
}

fun MafiaPhase.toMafiaPhaseRedisEntity(): MafiaPhaseRedisEntity {
    return when (this) {
        is MafiaPhase.Wait -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.WAIT,
        )

        is MafiaPhase.Ready -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.READY,
            turnList = turnList.map { player ->
                MafiaPlayerRedisEntity(
                    id = player.userId.value,
                    nickname = player.nickname,
                    color = player.color,
                    isConnect = player.isConnect(),
                )
            },
            mafiaPlayer = MafiaPlayerRedisEntity(
                id = mafiaPlayer.userId.value,
                nickname = mafiaPlayer.nickname,
                color = mafiaPlayer.color,
                isConnect = mafiaPlayer.isConnect(),
            ),
            keyword = MafiaKeywordRedisEntity(
                answer = keyword.answer,
                category = keyword.category,
            ),
        )

        is MafiaPhase.Playing -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.PLAYING,
            turnList = turnList.map { player ->
                MafiaPlayerRedisEntity(
                    id = player.userId.value,
                    nickname = player.nickname,
                    color = player.color,
                    isConnect = player.isConnect(),
                )
            },
            mafiaPlayer = MafiaPlayerRedisEntity(
                id = mafiaPlayer.userId.value,
                nickname = mafiaPlayer.nickname,
                color = mafiaPlayer.color,
                isConnect = mafiaPlayer.isConnect(),
            ),
            keyword = MafiaKeywordRedisEntity(
                answer = keyword.answer,
                category = keyword.category,
            ),
            drawData = drawData.map { pair ->
                DrawInfoRedisEntity(
                    userId = pair.first.value,
                    draw = pair.second,
                )
            },
            round = turnInfo.round,
            turn = turnInfo.turn,
        )

        is MafiaPhase.Vote -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.VOTE,
            turnList = turnList.map { player ->
                MafiaPlayerRedisEntity(
                    id = player.userId.value,
                    nickname = player.nickname,
                    color = player.color,
                    isConnect = player.isConnect(),
                )
            },
            mafiaPlayer = MafiaPlayerRedisEntity(
                id = mafiaPlayer.userId.value,
                nickname = mafiaPlayer.nickname,
                color = mafiaPlayer.color,
                isConnect = mafiaPlayer.isConnect(),
            ),
            keyword = MafiaKeywordRedisEntity(
                answer = keyword.answer,
                category = keyword.category,
            ),
            drawData = drawData.map { pair ->
                DrawInfoRedisEntity(
                    userId = pair.first.value,
                    draw = pair.second,
                )
            },
            players = serializePlayers(this.players),
        )

        is MafiaPhase.InferAnswer -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.INFER_ANSWER,
            turnList = turnList.map { player ->
                MafiaPlayerRedisEntity(
                    id = player.userId.value,
                    nickname = player.nickname,
                    color = player.color,
                    isConnect = player.isConnect(),
                )
            },
            mafiaPlayer = MafiaPlayerRedisEntity(
                id = mafiaPlayer.userId.value,
                nickname = mafiaPlayer.nickname,
                color = mafiaPlayer.color,
                isConnect = mafiaPlayer.isConnect(),
            ),
            keyword = MafiaKeywordRedisEntity(
                answer = keyword.answer,
                category = keyword.category,
            ),
            drawData = drawData.map { pair ->
                DrawInfoRedisEntity(
                    userId = pair.first.value,
                    draw = pair.second,
                )
            },
            answer = answer,
        )

        is MafiaPhase.End -> MafiaPhaseRedisEntity(
            status = RedisMafiaPhaseStatus.END,
            turnList = turnList.map { player ->
                MafiaPlayerRedisEntity(
                    id = player.userId.value,
                    nickname = player.nickname,
                    color = player.color,
                    isConnect = player.isConnect(),
                )
            },
            mafiaPlayer = MafiaPlayerRedisEntity(
                id = mafiaPlayer.userId.value,
                nickname = mafiaPlayer.nickname,
                color = mafiaPlayer.color,
                isConnect = mafiaPlayer.isConnect(),
            ),
            keyword = MafiaKeywordRedisEntity(
                answer = keyword.answer,
                category = keyword.category,
            ),
            drawData = drawData.map { pair ->
                DrawInfoRedisEntity(
                    userId = pair.first.value,
                    draw = pair.second,
                )
            },
            answer = answer,
            showAnswer = showAnswer,
            isMafiaWin = isMafiaWin,
        )
    }
}

fun MafiaPhaseRedisEntity.toDomain(): MafiaPhase = when (status) {
    RedisMafiaPhaseStatus.WAIT -> MafiaPhase.Wait
    RedisMafiaPhaseStatus.READY -> MafiaPhase.Ready(
        turnList = turnList!!.map { player ->
            player.toDomain()
        },
        mafiaPlayer = mafiaPlayer!!.toDomain(),
        keyword = keyword!!.toDomain(),
    )

    RedisMafiaPhaseStatus.PLAYING -> MafiaPhase.Playing(
        turnList = turnList!!.map { player ->
            player.toDomain()
        },
        mafiaPlayer = mafiaPlayer!!.toDomain(),
        keyword = keyword!!.toDomain(),
        turnInfo = TurnInfo(round!!, turn!!),
        drawData = drawData!!.map { item ->
            Pair(UserId(item.userId), item.draw)
        }.toMutableList(),
    )

    RedisMafiaPhaseStatus.VOTE -> MafiaPhase.Vote(
        turnList = turnList!!.map { player ->
            player.toDomain()
        },
        mafiaPlayer = mafiaPlayer!!.toDomain(),
        keyword = keyword!!.toDomain(),
        drawData = drawData!!.map { item ->
            Pair(UserId(item.userId), item.draw)
        }.toMutableList(),
        players = deserializePlayers(players!!),
    )

    RedisMafiaPhaseStatus.INFER_ANSWER -> MafiaPhase.InferAnswer(
        turnList = turnList!!.map { player ->
            player.toDomain()
        },
        mafiaPlayer = mafiaPlayer!!.toDomain(),
        keyword = keyword!!.toDomain(),
        drawData = drawData!!.map { item ->
            Pair(UserId(item.userId), item.draw)
        }.toMutableList(),
        answer = answer,
    )

    RedisMafiaPhaseStatus.END -> MafiaPhase.End(
        turnList = turnList!!.map { player ->
            player.toDomain()
        },
        mafiaPlayer = mafiaPlayer!!.toDomain(),
        keyword = keyword!!.toDomain(),
        drawData = drawData!!.map { item ->
            Pair(UserId(item.userId), item.draw)
        }.toMutableList(),
        answer = answer,
        showAnswer = showAnswer!!,
        isMafiaWin = isMafiaWin!!,
    )
}
