package com.xorker.draw.auth

enum class AuthType(val authPlatform: AuthPlatform) {
    APPLE_ID_TOKEN(AuthPlatform.APPLE),
    GOOGLE_ID_TOKEN(AuthPlatform.GOOGLE),
}
