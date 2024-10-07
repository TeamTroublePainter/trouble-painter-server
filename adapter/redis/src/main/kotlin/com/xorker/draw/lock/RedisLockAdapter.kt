package com.xorker.draw.lock

import com.xorker.draw.exception.NotFoundLockKeyException
import com.xorker.draw.exception.UnSupportedException
import java.time.Duration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
internal class RedisLockAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
) : LockRepository {

    override fun lock(key: String) {
        while (getLock(key).not()) {
            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                throw UnSupportedException
            }
        }
    }

    override fun unlock(key: String) {
        redisTemplate.delete(key + LOCK)
    }

    private fun getLock(key: String): Boolean {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(key + LOCK, LOCK, Duration.ofSeconds(1)) ?: throw NotFoundLockKeyException
    }

    companion object {
        private const val LOCK = "lock"
    }
}
