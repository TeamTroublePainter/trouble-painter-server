package com.xorker.draw.user

import com.xorker.draw.auth.AuthInfo
import com.xorker.draw.auth.AuthPlatform

interface UserRepository {
    fun getUser(platform: AuthPlatform, platformUserId: String): UserInfo?

    fun getUser(userId: UserId): UserInfo?

    fun getAuthInfo(userId: UserId): AuthInfo?

    fun createUser(platform: AuthPlatform, platformUserId: String, userName: String, email: String?): UserInfo

    fun createUser(userName: String?): UserInfo

    fun withdrawal(userId: UserId)

    fun updateNickname(userId: UserId, nickname: String): User
}
