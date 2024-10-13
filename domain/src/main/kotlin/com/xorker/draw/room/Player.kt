package com.xorker.draw.room

import com.xorker.draw.user.UserId

interface Player {
    val userId: UserId
    val nickname: String
    var isConnect: Boolean

    fun connect() {
        this.isConnect = true
    }

    fun disconnect() {
        this.isConnect = false
    }
}
