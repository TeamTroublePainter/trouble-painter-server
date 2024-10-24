package com.xorker.draw.oauth.google

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.google")
data class GoogleApiProperties(
    val clientId: String,
)
