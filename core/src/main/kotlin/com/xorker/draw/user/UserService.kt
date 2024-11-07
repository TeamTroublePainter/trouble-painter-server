package com.xorker.draw.user

import org.springframework.stereotype.Service

@Service
internal class UserService(
    private val userRepository: UserRepository,
) : UserUseCase {
    override fun getUserDetail(userId: UserId): UserDetail? {
        val userInfo = userRepository.getUser(userId) ?: return null

        val authInfo = userRepository.getAuthInfo(userId)

        return UserDetail(
            userId,
            userInfo.name,
            authInfo?.email,
            authInfo?.authPlatform,
        )
    }

    override fun updateUser(userId: UserId, nickname: String): User {
        return userRepository.updateNickname(userId, nickname)
    }
}
