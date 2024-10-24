package com.xorker.draw.user

interface UserUseCase {
    fun updateUser(userId: UserId, nickname: String): User
}
