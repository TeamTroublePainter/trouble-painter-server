package com.xorker.draw.mafia

import com.xorker.draw.exception.AlreadyWaitingUserException
import com.xorker.draw.exception.UnSupportedException
import com.xorker.draw.user.User
import com.xorker.draw.user.UserId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import org.springframework.stereotype.Component

@Component
internal class MafiaGameWaitingQueueAdapter : MafiaGameWaitingQueueRepository {
    private val waitingQueue: ConcurrentHashMap<String, ConcurrentLinkedQueue<User>> = ConcurrentHashMap()
    private val seen: ConcurrentHashMap<String, MutableSet<UserId>> = ConcurrentHashMap()

    override fun size(locale: String): Int {
        return waitingQueue[locale]?.size ?: 0
    }

    override fun enqueue(user: User, locale: String) {
        val queue = waitingQueue.getOrPut(locale) { ConcurrentLinkedQueue() }
        val seen = seen.getOrPut(locale) { ConcurrentHashMap.newKeySet() }

        if (seen.contains(user.id)) throw AlreadyWaitingUserException

        seen.add(user.id)
        queue.add(user)
    }

    override fun dequeue(locale: String): User {
        val queue = waitingQueue[locale] ?: throw UnSupportedException
        val seen = seen[locale] ?: throw UnSupportedException

        val user = queue.poll()
        seen.remove(user.id)

        return user
    }

    override fun remove(user: User, locale: String) {
        waitingQueue[locale]?.remove(user)
        seen[locale]?.remove(user.id)
    }
}
