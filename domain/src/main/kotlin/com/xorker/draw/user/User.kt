package com.xorker.draw.user

import com.xorker.draw.auth.AuthPlatform

@JvmInline
value class UserId(val value: Long)

data class User(
    val id: UserId,
    val name: String,
)

data class UserInfo(
    val id: UserId,
    val name: String?,
)

data class UserDetail(
    val id: UserId,
    val name: String?,
    val email: String?,
    val authPlatform: AuthPlatform?,
)
