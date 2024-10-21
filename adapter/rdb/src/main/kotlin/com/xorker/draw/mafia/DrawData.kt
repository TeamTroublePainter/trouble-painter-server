package com.xorker.draw.mafia

import com.xorker.draw.user.UserId

data class DrawData(
    val draw: MutableList<Pair<UserId, Map<String, Any>>>,
)
