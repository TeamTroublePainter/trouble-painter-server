package com.xorker.draw.lock

interface LockRepository {
    fun lock(key: String)
    fun unlock(key: String)
}
