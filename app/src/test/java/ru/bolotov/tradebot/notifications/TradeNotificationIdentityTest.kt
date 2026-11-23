package ru.bolotov.tradebot.notifications

import org.junit.Assert.*
import org.junit.Test

class TradeNotificationIdentityTest {
    @Test
    fun `same event replaces notification after replay but another event stays separate`() {
        val data = mapOf("eventType" to "TRADE_EXECUTED", "eventId" to "event-1")
        assertEquals("trade:event-1", tradeNotificationTag(data))
        assertEquals(tradeNotificationTag(data), tradeNotificationTag(data.toMap()))
        assertNotEquals(tradeNotificationTag(data), tradeNotificationTag(data + ("eventId" to "event-2")))
    }

    @Test
    fun `legacy lifecycle target keeps open and close notifications separate`() {
        val target = mapOf("eventType" to "TRADE_EXECUTED", "positionId" to "position")
        assertEquals("trade:position:OPEN", tradeNotificationTag(target + ("tradeAction" to "OPEN")))
        assertEquals("trade:position:CLOSE", tradeNotificationTag(target + ("tradeAction" to "CLOSE")))
        assertNull(tradeNotificationTag(emptyMap()))
    }
}
