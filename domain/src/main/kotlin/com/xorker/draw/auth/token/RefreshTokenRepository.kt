package com.xorker.draw.auth.token

import com.xorker.draw.user.UserId
import java.time.temporal.TemporalAmount

interface RefreshTokenRepository {
    fun getUserIdOrThrow(refreshToken: String): UserId

    fun createRefreshToken(userId: UserId, expiredTime: TemporalAmount): String

    fun deleteRefreshToken(userId: UserId)
}
