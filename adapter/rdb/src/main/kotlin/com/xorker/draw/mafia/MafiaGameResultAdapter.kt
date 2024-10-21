package com.xorker.draw.mafia

import com.fasterxml.jackson.databind.ObjectMapper
import com.xorker.draw.exception.NotFoundUserException
import com.xorker.draw.exception.NotFoundWordException
import com.xorker.draw.player.PlayerJpaEntity
import com.xorker.draw.player.ResultType
import com.xorker.draw.player.RoleType
import com.xorker.draw.user.UserJpaEntity
import com.xorker.draw.user.UserJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
internal class MafiaGameResultAdapter(
    private val objectMapper: ObjectMapper,
    private val userJpaRepository: UserJpaRepository,
    private val mafiaGameResultJpaRepository: MafiaGameResultJpaRepository,
    private val wordJpaRepository: WordJpaRepository,
) : MafiaGameResultRepository {

    @Transactional
    override fun saveMafiaGameResult(gameInfo: MafiaGameInfo) {
        println()
        println("save mafia game result")
        val room = gameInfo.room

        val phase = gameInfo.phase
        assertIs<MafiaPhase.End>(phase)

        val keyword = phase.keyword
        val word = wordJpaRepository.findByKeyword(keyword.answer) ?: throw NotFoundWordException

        val drawData = DrawData(phase.drawData)
        val draw = objectMapper.writeValueAsString(drawData)

        val gameResult = MafiaGameResultJpaEntity.of(room.locale, draw, phase.answer, word.id)

        val mafia = phase.mafiaPlayer
        room.players.forEach { player ->
            println(player.toString())
            val user = userJpaRepository.findByIdOrNull(player.userId.value) ?: throw NotFoundUserException
            println(user.id)
            val createPlayer = createPlayer(phase, mafia, player, user, gameResult)
            println(createPlayer.toString())
        }
        println()
        println("save save")

        mafiaGameResultJpaRepository.save(gameResult)
    }

    private fun createPlayer(
        phase: MafiaPhase.End,
        mafia: MafiaPlayer,
        player: MafiaPlayer,
        user: UserJpaEntity,
        gameResult: MafiaGameResultJpaEntity,
    ) = if (phase.isMafiaWin) {
        if (mafia.userId == player.userId) {
            PlayerJpaEntity.of(ResultType.MAFIA_WIN, RoleType.MAFIA, user, gameResult)
        } else {
            PlayerJpaEntity.of(ResultType.CITIZEN_LOSE, RoleType.CITIZEN, user, gameResult)
        }
    } else {
        if (mafia.userId == player.userId) {
            PlayerJpaEntity.of(ResultType.MAFIA_LOSE, RoleType.MAFIA, user, gameResult)
        } else {
            PlayerJpaEntity.of(ResultType.CITIZEN_WIN, RoleType.CITIZEN, user, gameResult)
        }
    }
}
