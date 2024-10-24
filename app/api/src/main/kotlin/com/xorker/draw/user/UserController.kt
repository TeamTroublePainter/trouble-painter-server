package com.xorker.draw.user

import com.xorker.draw.support.auth.NeedLogin
import com.xorker.draw.support.auth.PrincipalUser
import com.xorker.draw.user.dto.UpdateUserRequest
import com.xorker.draw.user.dto.UserDetailResponse
import com.xorker.draw.user.dto.UserResponse
import com.xorker.draw.user.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "유저 관련 API")
@RestController
class UserController(
    private val userUseCase: UserUseCase,
) {
    @Operation(summary = "유저 상세 정보 조회")
    @GetMapping("/api/v1/user")
    @NeedLogin
    fun getUserDetail(
        user: PrincipalUser,
    ): UserDetailResponse {
        return userUseCase.getUserDetail(user.userId).toResponse()
    }

    @Operation(summary = "유저 정보 수정")
    @PatchMapping("/api/v1/user")
    @NeedLogin
    fun updateNickname(
        user: PrincipalUser,
        @RequestBody request: UpdateUserRequest,
    ): UserResponse {
        return userUseCase.updateUser(user.userId, request.nickname).toResponse()
    }
}
