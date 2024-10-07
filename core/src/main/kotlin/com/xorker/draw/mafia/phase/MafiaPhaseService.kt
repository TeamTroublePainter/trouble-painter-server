package com.xorker.draw.mafia.phase

import com.xorker.draw.exception.InvalidRequestValueException
import com.xorker.draw.exception.NotFoundRoomException
import com.xorker.draw.lock.LockRepository
import com.xorker.draw.mafia.MafiaGameInfo
import com.xorker.draw.mafia.MafiaGameRepository
import com.xorker.draw.mafia.MafiaPhase
import com.xorker.draw.mafia.MafiaPhaseMessenger
import com.xorker.draw.mafia.assert
import com.xorker.draw.mafia.assertIs
import com.xorker.draw.room.RoomId
import com.xorker.draw.user.User
import org.springframework.stereotype.Service

@Service
internal class MafiaPhaseService(
    private val mafiaGameRepository: MafiaGameRepository,
    private val mafiaPhaseStartGameProcessor: MafiaPhaseStartGameProcessor,
    private val mafiaPhasePlayGameProcessor: MafiaPhasePlayGameProcessor,
    private val mafiaPhasePlayVoteProcessor: MafiaPhasePlayVoteProcessor,
    private val mafiaPhaseInferAnswerProcessor: MafiaPhaseInferAnswerProcessor,
    private val mafiaPhaseEndGameProcessor: MafiaPhaseEndGameProcessor,
    private val mafiaPhaseMessenger: MafiaPhaseMessenger,
    private val lockRepository: LockRepository,
) : MafiaPhaseUseCase {

    override fun startGame(user: User): MafiaPhase.Ready {
        val gameInfo = mafiaGameRepository.getGameInfo(user.id) ?: throw InvalidRequestValueException
        return startGame(gameInfo)
    }

    override fun startGame(roomId: RoomId): MafiaPhase.Ready {
        val gameInfo = getGameInfo(roomId)
        return startGame(gameInfo)
    }

    private fun startGame(gameInfo: MafiaGameInfo): MafiaPhase.Ready {
        val roomId = gameInfo.room.id

        lockRepository.lock(roomId.value)

        assertIs<MafiaPhase.Wait>(gameInfo.phase)

        val phase = mafiaPhaseStartGameProcessor.startMafiaGame(gameInfo) { playGame(roomId) }

        lockRepository.unlock(roomId.value)

        mafiaPhaseMessenger.broadcastPhase(gameInfo)

        return phase
    }

    override fun playGame(roomId: RoomId): MafiaPhase.Playing {
        val gameInfo = getGameInfo(roomId)

        lockRepository.lock(roomId.value)

        val readyPhase = gameInfo.phase
        assertIs<MafiaPhase.Ready>(readyPhase)

        val phase = mafiaPhasePlayGameProcessor.playMafiaGame(gameInfo) { vote(roomId) }

        lockRepository.unlock(roomId.value)

        mafiaPhaseMessenger.broadcastPhase(gameInfo)

        return phase
    }

    override fun vote(roomId: RoomId): MafiaPhase.Vote {
        val gameInfo = getGameInfo(roomId)

        lockRepository.lock(roomId.value)

        val playingPhase = gameInfo.phase
        assertIs<MafiaPhase.Playing>(playingPhase)

        val phase = mafiaPhasePlayVoteProcessor.playVote(
            gameInfo,
            {
                interAnswer(roomId)
            },
            {
                endGame(roomId)
            },
        )

        lockRepository.unlock(roomId.value)

        mafiaPhaseMessenger.broadcastPhase(gameInfo)

        return phase
    }

    override fun interAnswer(roomId: RoomId): MafiaPhase.InferAnswer {
        val gameInfo = getGameInfo(roomId)

        lockRepository.lock(roomId.value)

        val votePhase = gameInfo.phase
        assertIs<MafiaPhase.Vote>(votePhase)

        val phase = mafiaPhaseInferAnswerProcessor.playInferAnswer(gameInfo) { endGame(roomId) }

        lockRepository.unlock(roomId.value)

        mafiaPhaseMessenger.broadcastPhase(gameInfo)

        return phase
    }

    override fun endGame(roomId: RoomId): MafiaPhase.End {
        val gameInfo = getGameInfo(roomId)

        lockRepository.lock(roomId.value)

        val votePhase = gameInfo.phase
        assert<MafiaPhase.Vote, MafiaPhase.InferAnswer>(votePhase)

        val phase = mafiaPhaseEndGameProcessor.endGame(gameInfo)

        lockRepository.unlock(roomId.value)

        mafiaPhaseMessenger.broadcastPhase(gameInfo)

        return phase
    }

    private fun getGameInfo(roomId: RoomId): MafiaGameInfo {
        return mafiaGameRepository.getGameInfo(roomId) ?: throw NotFoundRoomException
    }
}
