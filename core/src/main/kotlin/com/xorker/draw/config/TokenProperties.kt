package com.xorker.draw.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(value = "token")
data class TokenProperties(
    val accessTokenExpirationHour: String,
    val refreshTokenExpirationMonth: String,
    val anonymousExpirationYear: String,
)
