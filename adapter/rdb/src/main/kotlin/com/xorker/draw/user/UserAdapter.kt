package com.xorker.draw.user

import com.xorker.draw.auth.AuthInfo
import com.xorker.draw.auth.AuthPlatform
import com.xorker.draw.auth.AuthUserJpaEntity
import com.xorker.draw.auth.AuthUserJpaRepository
import com.xorker.draw.exception.NotFoundUserException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
internal class UserAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val authUserJpaRepository: AuthUserJpaRepository,
) : UserRepository {
    override fun getUser(platform: AuthPlatform, platformUserId: String): UserInfo? =
        authUserJpaRepository.find(platform, platformUserId)?.user?.toDomain()

    override fun getUser(userId: UserId): UserInfo? =
        userJpaRepository.findByIdOrNull(userId.value)?.toDomain()

    override fun getAuthInfo(userId: UserId): AuthInfo? {
        return authUserJpaRepository.findByUserId(userId.value)?.toDomain()
    }

    override fun createUser(platform: AuthPlatform, platformUserId: String, userName: String, email: String?): UserInfo {
        val user = UserJpaEntity()
        val authUser = authUserJpaRepository.save(AuthUserJpaEntity.of(platform, platformUserId, user, email))
        return authUser.user.toDomain()
    }

    override fun createUser(userName: String?): UserInfo {
        val user = UserJpaEntity.of(userName)
        val savedUser = userJpaRepository.save(user)
        return savedUser.toDomain()
    }

    @Transactional
    override fun withdrawal(userId: UserId) {
        val user = userJpaRepository.findByIdOrNull(userId.value) ?: throw NotFoundUserException
        user.withdrawal()
        userJpaRepository.save(user)
    }

    @Transactional
    override fun updateNickname(userId: UserId, nickname: String): User {
        val user = userJpaRepository.findByIdOrNull(userId.value) ?: throw NotFoundUserException

        user.name = nickname
        return userJpaRepository.save(user).toUser()
    }

    private fun AuthUserJpaEntity.toDomain(): AuthInfo = AuthInfo(
        this.platform,
        this.email,
    )
}
