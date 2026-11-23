package ru.bolotov.feature.dashboard.api

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class FinancialDecodeTest {
    @Test
    fun `nullable metrics retain unknown and denominator from executor`() {
        val metrics = Json.decodeFromString<DtoBot.DashboardMetrics>(
            """{"realizedPnl":null,"dailyPnl":null,"winRate":null,"closedTradesCount":1,"knownPnlTradesCount":0,"unknownPnlTradesCount":1}"""
        )
        assertNull(metrics.realizedPnl)
        assertNull(metrics.winRate)
        assertEquals(0, metrics.knownPnlTradesCount)
        assertEquals(1, metrics.unknownPnlTradesCount)
    }

    @Test
    fun `older numeric payload without denominator remains readable`() {
        val metrics = Json.decodeFromString<DtoBot.DashboardMetrics>(
            """{"realizedPnl":4.0,"dailyPnl":0.0,"winRate":50.0,"closedTradesCount":2}"""
        )
        assertEquals(4.0, metrics.realizedPnl)
        assertNull(metrics.knownPnlTradesCount)
    }
}
