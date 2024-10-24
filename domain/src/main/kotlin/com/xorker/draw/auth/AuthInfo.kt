package com.xorker.draw.auth

data class AuthInfo(
    val authPlatform: AuthPlatform,
    val email: String?,
)
