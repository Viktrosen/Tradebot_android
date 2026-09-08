package ru.bolotov.feature.positions.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.bolotov.feature.positions.api.OpenPositionResponse
import ru.bolotov.feature.positions.api.PositionsResponse

class PositionsMapperTest {

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
