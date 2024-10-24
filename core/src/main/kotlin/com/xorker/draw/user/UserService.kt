package com.xorker.draw.user

import org.springframework.stereotype.Service

@Service
internal class UserService(
    private val userRepository: UserRepository,
) : UserUseCase {
    override fun updateUser(userId: UserId, nickname: String): User {
        return userRepository.updateNickname(userId, nickname)
    }
}
