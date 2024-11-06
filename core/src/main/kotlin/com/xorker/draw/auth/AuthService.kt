package com.xorker.draw.auth

import com.xorker.draw.auth.token.AccessTokenRepository
import com.xorker.draw.auth.token.RefreshTokenRepository
import com.xorker.draw.auth.token.Token
import com.xorker.draw.config.TokenProperties
import com.xorker.draw.exception.AlreadyLinkedAccountException
import com.xorker.draw.user.UserId
import com.xorker.draw.user.UserInfo
import com.xorker.draw.user.UserRepository
import java.time.Duration
import java.time.Period
import java.time.temporal.TemporalAmount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AuthService(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenProperties: TokenProperties,
) : AuthUseCase {

    @Transactional
    override fun signIn(authType: AuthType, token: String): Token {
        val platformUserId = authRepository.getPlatformUserId(authType, token)
        val user = userRepository.getUser(authType.authPlatform, platformUserId) ?: createUser(authType, platformUserId)

        return createToken(
            userId = user.id,
            accessTokenExpirationTime = Duration.ofHours(tokenProperties.accessTokenExpirationHour),
            refreshTokenExpirationTime = Period.ofMonths(tokenProperties.refreshTokenExpirationMonth),
        )
    }

    override fun anonymousSignIn(): Token {
        val user = userRepository.createUser(null)

        return createToken(
            userId = user.id,
            accessTokenExpirationTime = Period.ofYears(tokenProperties.anonymousExpirationYear),
            refreshTokenExpirationTime = Period.ofYears(tokenProperties.anonymousExpirationYear),
        )
    }

    override fun reissue(refreshToken: String): Token {
        val userId = refreshTokenRepository.getUserIdOrThrow(refreshToken)

        return createToken(
            userId = userId,
            accessTokenExpirationTime = Duration.ofHours(tokenProperties.accessTokenExpirationHour),
            refreshTokenExpirationTime = Period.ofMonths(tokenProperties.refreshTokenExpirationMonth),
        )
    }

    @Transactional
    override fun withdrawal(userId: UserId) {
        refreshTokenRepository.deleteRefreshToken(userId)
        userRepository.withdrawal(userId)
    }

    @Transactional
    override fun transfer(userId: UserId, authType: AuthType, token: String): Token {
        val platformUserId = authRepository.getPlatformUserId(authType, token)

        validateIsAnonymousUser(authType, platformUserId, userId)

        val user = userRepository.transfer(userId, authType.authPlatform, platformUserId)

        return createToken(
            userId = user.id,
            accessTokenExpirationTime = Duration.ofHours(tokenProperties.accessTokenExpirationHour),
            refreshTokenExpirationTime = Period.ofMonths(tokenProperties.refreshTokenExpirationMonth),
        )
    }

    private fun createUser(authType: AuthType, platformUserId: String): UserInfo {
        val userName = authRepository.getPlatformUserName(authType, platformUserId)

        return userRepository.createUser(authType.authPlatform, platformUserId, userName)
    }

    private fun validateIsAnonymousUser(
        authType: AuthType,
        platformUserId: String,
        userId: UserId,
    ) {
        val findUser = userRepository.getUser(authType.authPlatform, platformUserId)
        if (findUser != null) throw AlreadyLinkedAccountException

        val authInfo = userRepository.getAuthInfo(userId)
        if (authInfo != null) throw AlreadyLinkedAccountException
    }

    private fun createToken(
        userId: UserId,
        accessTokenExpirationTime: TemporalAmount,
        refreshTokenExpirationTime: TemporalAmount,
    ): Token {
        return Token(
            accessToken = accessTokenRepository.createAccessToken(
                userId = userId,
                expiredTime = accessTokenExpirationTime,
            ),
            refreshToken = refreshTokenRepository.createRefreshToken(
                userId = userId,
                expiredTime = refreshTokenExpirationTime,
            ),
            userId = userId,
        )
    }
}
