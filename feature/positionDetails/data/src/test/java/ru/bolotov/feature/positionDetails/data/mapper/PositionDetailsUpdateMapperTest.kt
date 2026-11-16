package ru.bolotov.feature.positionDetails.data.mapper

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate

class PositionDetailsUpdateMapperTest {
    @Test
    fun `quote must match the lifecycle not just the instrument`() {
        val quote =
            TradingStateUpdate.PositionPrice(
                instrumentId = "same-instrument",
                currentPrice = 10.0,
                unrealizedPnl = 1.0,
                pnlPercent = 2.0,
                positionId = "old",
            )
        assertNull(quote.toDetailsUpdate("new"))
        assertNull(quote.copy(positionId = null).toDetailsUpdate("new"))
        assertTrue(quote.toDetailsUpdate("old") is PositionDetailsUpdate.Quote)
        assertEquals(
            PositionDetailsUpdate.RefreshRequired,
            TradingStateUpdate.Reconnected.toDetailsUpdate("new"),
        )
        assertEquals(
            PositionDetailsUpdate.RefreshRequired,
            TradingStateUpdate.PositionsChanged.toDetailsUpdate("new"),
        )
    }
}
