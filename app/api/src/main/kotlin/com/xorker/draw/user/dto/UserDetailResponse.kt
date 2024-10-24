package com.xorker.draw.user.dto

import com.xorker.draw.auth.AuthPlatform
import com.xorker.draw.user.UserDetail
import com.xorker.draw.user.UserId
import io.swagger.v3.oas.annotations.media.Schema

data class UserDetailResponse(
    val id: UserId,
    val nickname: String?,

    @Schema(description = "null 일 경우 게스트")
    val authPlatform: AuthPlatform?,
    val email: String?,
)

fun UserDetail.toResponse(): UserDetailResponse = UserDetailResponse(
    id = this.id,
    nickname = this.name,
    authPlatform = this.authPlatform,
    email = this.email,
)
