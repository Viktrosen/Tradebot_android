package ru.bolotov.feature.positions.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.bolotov.feature.positions.api.OpenPositionResponse
import ru.bolotov.feature.positions.api.PositionsResponse

class PositionsMapperTest {
    @Test
    fun `unknown historical prices and result stay null without a fabricated percentage`() {
        val response = PositionsResponse(
            openPositions = emptyList(),
            closedTrades = listOf(ru.bolotov.feature.positions.api.ClosedPositionResponse(
                positionId = "old",
                instrumentId = "instrument",
                instrumentName = "TEST",
                direction = "SELL",
                entryPrice = null,
                entryTime = null,
                closePrice = null,
                quantity = 1,
                lotSize = 10,
                realizedPnl = null,
                closedAt = "2026-11-23T00:00:00Z"
            ))
        )
        val position = response.toDomain().single()
        org.junit.Assert.assertNull(position.entryPrice)
        org.junit.Assert.assertNull(position.currentPrice)
        org.junit.Assert.assertNull(position.pnl)
        org.junit.Assert.assertNull(position.pnlPercent)
    }

    @Test
    fun `maps optional profit protection fields for an open position`() {
        val response = PositionsResponse(
            openPositions = listOf(
                OpenPositionResponse(
                    positionId = "position-1",
                    instrumentId = "instrument-1",
                    instrumentName = "Тест",
                    direction = "BUY",
                    entryPrice = 100.0,
                    quantity = 1,
                    lotSize = 1,
                    entryTime = "2026-09-08T10:00:00Z",
                    brokerStopLossPrice = 97.0,
                    managedExitPrice = 101.5,
                    profitProtectionStage = "TRAILING"
                )
            ),
            closedTrades = emptyList()
        )

        val position = response.toDomain().single()

        assertEquals(97.0, position.brokerStopLossPrice)
        assertEquals(101.5, position.managedExitPrice)
        assertEquals("TRAILING", position.profitProtectionStage)
    }
}
