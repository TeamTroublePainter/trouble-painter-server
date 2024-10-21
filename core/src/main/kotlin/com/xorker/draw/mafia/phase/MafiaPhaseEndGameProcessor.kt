package com.xorker.draw.mafia.phase

import com.xorker.draw.event.mafia.MafiaGameInfoEventProducer
import com.xorker.draw.exception.InvalidMafiaPhaseException
import com.xorker.draw.mafia.MafiaGameInfo
import com.xorker.draw.mafia.MafiaGameRepository
import com.xorker.draw.mafia.MafiaGameResultRepository
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.assertIs
import com.xorker.draw.timer.TimerRepository
import java.time.Duration
import org.springframework.stereotype.Component

@Component
internal class MafiaPhaseEndGameProcessor(
    private val mafiaGameRepository: MafiaGameRepository,
    private val timerRepository: TimerRepository,
    private val mafiaGameResultRepository: MafiaGameResultRepository,
    private val mafiaGameInfoEventProducer: MafiaGameInfoEventProducer,
) {

    internal fun endGame(gameInfo: MafiaGameInfo): MafiaPhase.End {
        val phase = gameInfo.phase

        val gameOption = gameInfo.gameOption

        val room = gameInfo.room

        val endPhase = assertAndGetEndPhase(phase)

        judgeGameResult(endPhase)

        gameInfo.phase = endPhase

        mafiaGameRepository.saveGameInfo(gameInfo)

        mafiaGameResultRepository.saveMafiaGameResult(gameInfo)

        if (room.isRandomMatching) {
            timerRepository.startTimer(room.id, Duration.ofMillis(1)) {
                mafiaGameRepository.removeGameInfo(gameInfo)
            }
        } else {
            timerRepository.startTimer(room.id, gameOption.endTime) {
                processEndGame(gameInfo)
            }
        }
        return endPhase
    }

    private fun assertAndGetEndPhase(phase: MafiaPhase): MafiaPhase.End {
        return when (phase) {
            is MafiaPhase.Vote -> {
                phase.toEnd()
            }

            is MafiaPhase.InferAnswer -> {
                phase.toEnd()
            }

            else -> {
                throw InvalidMafiaPhaseException("유효하지 않는 Phase 입니다. 기대값: ${MafiaPhase.Vote::class}, ${MafiaPhase.InferAnswer::class}, 요청값: $phase")
            }
        }
    }

    private fun processEndGame(gameInfo: MafiaGameInfo) {
        val phase = gameInfo.phase
        assertIs<MafiaPhase.End>(phase)

        val room = gameInfo.room

        val players = room.players
        val owner = room.owner

        val joinPlayers = players.filter { it.isConnect() }
            .toList()

        if (joinPlayers.isEmpty()) return

        if (joinPlayers.contains(owner).not()) {
            room.owner = joinPlayers.first()
        }

        room.clear()
        room.addAll(joinPlayers)

        gameInfo.phase = MafiaPhase.Wait

        mafiaGameRepository.saveGameInfo(gameInfo)

        mafiaGameInfoEventProducer.changePhase(gameInfo)
    }

    private fun judgeGameResult(endPhase: MafiaPhase.End) {
        val showAnswer = endPhase.showAnswer

        if (showAnswer.not()) {
            endPhase.isMafiaWin = true
            return
        }

        val keyword = endPhase.keyword

        endPhase.isMafiaWin = endPhase.answer?.let {
            val trimmedAnswer = keyword.answer.replace(" ", "")
            val trimmedMafiaAnswer = it.replace(" ", "")

            trimmedAnswer.lowercase() == trimmedMafiaAnswer.lowercase() // TODO 동의어 처리
        } ?: false
    }
}
