package com.xorker.draw.mafia

import com.xorker.draw.mafia.dto.DrawRequest
import com.xorker.draw.user.UserId
import com.xorker.draw.websocket.Session

interface MafiaGameUseCase {
    fun draw(session: Session, request: DrawRequest)
    fun nextTurnByUser(session: Session)
    fun voteMafia(session: Session, targetUserId: UserId)
}
