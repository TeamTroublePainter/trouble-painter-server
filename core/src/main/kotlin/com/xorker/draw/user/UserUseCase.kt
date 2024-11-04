package com.xorker.draw.user

interface UserUseCase {
    fun getUserDetail(userId: UserId): UserDetail

    fun updateUser(userId: UserId, nickname: String): User
}
