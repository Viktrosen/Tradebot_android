package ru.bolotov.feature.positions.presentation.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для UiPosition presentation model.
 *
 * Проверяют:
 * - корректность расчёта isProfit и isLoss
 * - корректность infoExplanation
 * - equality и hashCode
 */
class UiPositionTest {

    @Test
    fun `UiPosition must calculate isProfit correctly for positive pnl`() {
        val position = UiPosition(
            id = "pos-1",
            instrumentName = "SBER",
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            entryPrice = 150.0,
            currentPrice = 155.0,
            entryTime = "2026-09-01T10:00:00Z",
            entryStrategyName = "CrossEma",
            aiExplanation = "AI approved",
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-1"
        )

        assertTrue(position.isProfit)
        assertFalse(position.isLoss)
    }

    @Test
    fun `UiPosition must calculate isLoss correctly for negative pnl`() {
        val position = UiPosition(
            id = "pos-2",
            instrumentName = "GAZP",
            pnl = -200.0,
            pnlPercent = -1.5,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 50,
            entryPrice = 180.0,
            currentPrice = 176.0,
            entryTime = "2026-09-01T11:00:00Z",
            entryStrategyName = "Voting",
            aiExplanation = null,
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-2"
        )

        assertFalse(position.isProfit)
        assertTrue(position.isLoss)
    }

    @Test
    fun `UiPosition must be neutral when pnl is zero`() {
        val position = UiPosition(
            id = "pos-3",
            instrumentName = "YNDX",
            pnl = 0.0,
            pnlPercent = 0.0,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 25,
            entryPrice = 3000.0,
            currentPrice = 3000.0,
            entryTime = "2026-09-01T09:00:00Z",
            entryStrategyName = "RSI",
            aiExplanation = null,
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-3"
        )

        assertFalse(position.isProfit)
        assertFalse(position.isLoss)
    }

    @Test
    fun `UiPosition must use aiExplanation for open positions`() {
        val position = UiPosition(
            id = "pos-4",
            instrumentName = "T",
            pnl = 100.0,
            pnlPercent = 2.0,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 200,
            entryPrice = 50.0,
            currentPrice = 50.5,
            entryTime = "2026-09-01T10:00:00Z",
            entryStrategyName = "MACD",
            aiExplanation = "AI approved",
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-4"
        )

        assertEquals("AI approved", position.infoExplanation)
    }

    @Test
    fun `UiPosition must use closeExplanation for closed positions`() {
        val position = UiPosition(
            id = "pos-5",
            instrumentName = "SBER",
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = true,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            entryPrice = 150.0,
            currentPrice = 155.0,
            entryTime = "2026-09-01T08:00:00Z",
            entryStrategyName = "CrossEma",
            aiExplanation = "AI approved",
            closeExplanation = "Take profit hit",
            closeTime = "2026-09-01T14:00:00Z",
            instrumentId = "instrument-5"
        )

        assertEquals("Take profit hit", position.infoExplanation)
    }

    @Test
    fun `UiPosition must handle null pnl as neutral`() {
        val position = UiPosition(
            id = "pos-6",
            instrumentName = "GAZP",
            pnl = null,
            pnlPercent = null,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 50,
            entryPrice = 180.0,
            currentPrice = null,
            entryTime = "2026-09-01T11:00:00Z",
            entryStrategyName = null,
            aiExplanation = null,
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-6"
        )

        // null pnl treated as 0.0, so neither profit nor loss
        assertFalse(position.isProfit)
        assertFalse(position.isLoss)
    }

    @Test
    fun `UiPosition must be equal when all fields match`() {
        val position1 = UiPosition(
            id = "pos-1",
            instrumentName = "SBER",
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            entryPrice = 150.0,
            currentPrice = 155.0,
            entryTime = "2026-09-01T10:00:00Z",
            entryStrategyName = "CrossEma",
            aiExplanation = "AI approved",
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-1"
        )

        val position2 = UiPosition(
            id = "pos-1",
            instrumentName = "SBER",
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            entryPrice = 150.0,
            currentPrice = 155.0,
            entryTime = "2026-09-01T10:00:00Z",
            entryStrategyName = "CrossEma",
            aiExplanation = "AI approved",
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-1"
        )

        assertEquals(position1, position2)
    }

    @Test
    fun `UiPosition must support copy with modified fields`() {
        val position = UiPosition(
            id = "pos-1",
            instrumentName = "SBER",
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            entryPrice = 150.0,
            currentPrice = 155.0,
            entryTime = "2026-09-01T10:00:00Z",
            entryStrategyName = "CrossEma",
            aiExplanation = "AI approved",
            closeExplanation = null,
            closeTime = null,
            instrumentId = "instrument-1"
        )

        val closedPosition = position.copy(
            isClosed = true,
            closeTime = "2026-09-01T15:00:00Z",
            closeExplanation = "Stop loss"
        )

        assertTrue(closedPosition.isClosed)
        assertEquals("Stop loss", closedPosition.infoExplanation)
    }
}
