package com.xorker.draw.player

enum class ResultType(
    val description: String,
) {
    MAFIA_WIN("마피아 승리"),
    MAFIA_LOSE("마피아 패배"),
    CITIZEN_WIN("시민 승리"),
    CITIZEN_LOSE("시민 패배"),
    ;
}
