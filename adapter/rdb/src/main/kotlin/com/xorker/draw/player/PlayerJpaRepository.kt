package com.xorker.draw.player

import org.springframework.data.jpa.repository.JpaRepository

internal interface PlayerJpaRepository : JpaRepository<PlayerJpaEntity, Long>
