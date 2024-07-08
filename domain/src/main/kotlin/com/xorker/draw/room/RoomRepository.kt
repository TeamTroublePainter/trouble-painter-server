package com.xorker.draw.room

interface RoomRepository {
    fun getRoom(roomId: RoomId): Room?
    fun saveRoom(room: Room)
}