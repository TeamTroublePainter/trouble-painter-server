package com.xorker.draw.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface AuthUserJpaRepository : JpaRepository<AuthUserJpaEntity, Long> {

    @Query(
        "select au from AuthUserJpaEntity au " +
            "join fetch au.user " +
            "where au.platformUserId = :platformUserId " +
            "and au.platform=:platform ",
    )
    fun find(platform: AuthPlatform, platformUserId: String): AuthUserJpaEntity?

    fun findByUserId(userId: Long): AuthUserJpaEntity

    @Modifying
    @Query(
        "delete from AuthUserJpaEntity au " +
            "where au.user.id = :userId",
    )
    fun deleteAllByUserId(@Param(value = "userId") userId: Long)
}
