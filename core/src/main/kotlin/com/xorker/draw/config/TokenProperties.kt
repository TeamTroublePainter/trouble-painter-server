package com.xorker.draw.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(value = "token")
data class TokenProperties(
    val accessTokenExpirationHour: Long,
    val refreshTokenExpirationMonth: Int,
    val anonymousExpirationYear: Int,
)
