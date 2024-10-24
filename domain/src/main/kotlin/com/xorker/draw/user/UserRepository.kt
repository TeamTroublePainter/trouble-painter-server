package com.xorker.draw.user

import com.xorker.draw.auth.AuthPlatform

interface UserRepository {
    fun getUser(platform: AuthPlatform, platformUserId: String): UserInfo?

    fun getUser(userId: UserId): UserInfo?

    fun createUser(platform: AuthPlatform, platformUserId: String, userName: String): UserInfo

    fun createUser(userName: String?): UserInfo

    fun withdrawal(userId: UserId)
}
