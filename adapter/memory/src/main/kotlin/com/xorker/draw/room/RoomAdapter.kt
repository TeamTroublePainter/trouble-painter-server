package com.xorker.draw.room

import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Component

@Component
internal class RoomAdapter : RoomRepository {
    private val roomMap = ConcurrentHashMap<RoomId, Room<Player>>()

    override fun saveRoom(room: Room<Player>) {
        if (room.isEmpty()) {
            roomMap.remove(room.id)
        } else {
            roomMap[room.id] = room
        }
    }

    override fun getRoom(roomId: RoomId): Room<Player>? {
        return roomMap[roomId]
    }
}
