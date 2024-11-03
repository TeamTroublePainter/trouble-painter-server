package com.xorker.draw.player

import org.springframework.data.jpa.repository.JpaRepository

internal interface PlayerHistoryJpaRepository : JpaRepository<PlayerHistoryJpaEntity, Long>
