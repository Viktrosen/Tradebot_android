package ru.bolotov.tradebot.notifications

/** Stable across process restarts; OPEN and CLOSE retain different identities. */
internal fun tradeNotificationTag(data: Map<String, String>): String? {
    if (data["eventType"] != "TRADE_EXECUTED") return null
    val identity = data["eventId"] ?: data["positionId"]?.let { position ->
        data["tradeAction"]?.let { action -> "$position:$action" }
    }
    return identity?.takeIf { it.isNotBlank() && it.length <= 128 }?.let { "trade:$it" }
}
