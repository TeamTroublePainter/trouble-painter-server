package com.xorker.draw.user.dto

import com.xorker.draw.user.User
import com.xorker.draw.user.UserId

data class UserResponse(
    val id: UserId,
    val nickname: String,
)

fun User.toResponse(): UserResponse = UserResponse(
    id = this.id,
    nickname = this.name,
)
