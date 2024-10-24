package com.xorker.draw.oauth.google

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GoogleApiProperties::class)
class GoogleApiConfiguration
