package com.xorker.draw.auth

interface AuthRepository {
    fun getPlatformUserId(authType: AuthType, token: String): String

    fun getPlatformUserName(authType: AuthType, platformUserId: String): String

    fun getPlatformEmail(authType: AuthType, platformUserId: String, token: String): String?
}
