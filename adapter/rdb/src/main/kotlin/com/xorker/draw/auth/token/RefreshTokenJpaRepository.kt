package com.xorker.draw.auth.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

internal interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, ByteArray> {
    @Modifying
    fun deleteByUserId(userId: Long)
}
