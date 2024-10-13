package com.xorker.draw.mafia

import com.xorker.draw.room.Player
import com.xorker.draw.user.UserId

class MafiaPlayer(
    override val userId: UserId,
    override val nickname: String,
    val color: String,
    override var isConnect: Boolean = true,
) : Player
