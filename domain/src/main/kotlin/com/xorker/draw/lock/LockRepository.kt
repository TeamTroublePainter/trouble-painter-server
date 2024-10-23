package com.xorker.draw.lock

interface LockRepository {
    fun <R> lock(key: String, call: () -> R): R
}
