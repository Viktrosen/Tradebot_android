package ru.bolotov.feature.positions.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для Position модели.
 *
 * Проверяют:
 * - корректность создания позиции
 * - расчёт P&L
 * - определение стороны позиции
 * - статус закрытия
 */
class PositionTest {

    @Test
    fun `Position must have all required fields`() {
        val position = Position(
            id = "pos-1",
            instrumentId = "instrument-1",
            instrumentName = "SBER",
            direction = "BUY",
            positionSide = "LONG",
            quantity = 100,
            lotSize = 10,
            entryPrice = 150.0,
            currentPrice = 155.0,
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = "CrossEma",
            aiExplanation = null,
            closeExplanation = null
        )

        assertEquals("pos-1", position.id)
        assertEquals("SBER", position.instrumentName)
        assertEquals(100, position.quantity)
        assertEquals(10, position.lotSize)
        assertEquals(150.0, position.entryPrice, 0.001)
        assertEquals(155.0, position.currentPrice!!, 0.001)
        assertEquals(500.0, position.pnl!!, 0.001)
        assertEquals(3.33, position.pnlPercent!!, 0.001)
        assertFalse(position.isClosed)
        assertEquals("CrossEma", position.entryStrategyName)
    }

    @Test
    fun `Position must support SHORT side`() {
        val position = Position(
            id = "pos-2",
            instrumentId = "instrument-2",
            instrumentName = "GAZP",
            direction = "SELL",
            positionSide = "SHORT",
            quantity = 50,
            lotSize = 10,
            entryPrice = 180.0,
            currentPrice = 175.0,
            pnl = 250.0,
            pnlPercent = 2.78,
            isClosed = false,
            entryTime = "2026-09-01T11:00:00Z",
            closeTime = null,
            entryStrategyName = "Voting",
            aiExplanation = null,
            closeExplanation = null
        )

        assertEquals("SHORT", position.positionSide)
        assertEquals("SELL", position.direction)
    }

    @Test
    fun `Closed position must have closeTime`() {
        val closedPosition = Position(
            id = "pos-3",
            instrumentId = "instrument-3",
            instrumentName = "YNDX",
            direction = "BUY",
            positionSide = "LONG",
            quantity = 25,
            lotSize = 1,
            entryPrice = 3000.0,
            currentPrice = 3000.0,
            pnl = 100.0,
            pnlPercent = 1.33,
            isClosed = true,
            entryTime = "2026-09-01T09:00:00Z",
            closeTime = "2026-09-01T14:00:00Z",
            entryStrategyName = "RSI",
            aiExplanation = "AI approved",
            closeExplanation = "Take profit hit"
        )

        assertTrue(closedPosition.isClosed)
        assertNotNull(closedPosition.closeTime)
        assertEquals("Take profit hit", closedPosition.closeExplanation)
        assertEquals("AI approved", closedPosition.aiExplanation)
    }

    @Test
    fun `Position with null currentPrice must not crash`() {
        val position = Position(
            id = "pos-4",
            instrumentId = "instrument-4",
            instrumentName = "T",
            direction = "BUY",
            quantity = 200,
            lotSize = 50,
            entryPrice = 50.0,
            currentPrice = null,
            pnl = null,
            pnlPercent = null,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = null,
            aiExplanation = null,
            closeExplanation = null
        )

        assertNull(position.currentPrice)
        assertNull(position.pnl)
        assertNull(position.pnlPercent)
    }

    @Test
    fun `Position must be equal when all fields match`() {
        val position1 = Position(
            id = "pos-1",
            instrumentId = "instrument-1",
            instrumentName = "SBER",
            direction = "BUY",
            quantity = 100,
            lotSize = 10,
            entryPrice = 150.0,
            currentPrice = 155.0,
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = "CrossEma",
            aiExplanation = null,
            closeExplanation = null
        )

        val position2 = Position(
            id = "pos-1",
            instrumentId = "instrument-1",
            instrumentName = "SBER",
            direction = "BUY",
            quantity = 100,
            lotSize = 10,
            entryPrice = 150.0,
            currentPrice = 155.0,
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = "CrossEma",
            aiExplanation = null,
            closeExplanation = null
        )

        assertEquals(position1, position2)
    }

    @Test
    fun `Position must have correct hashCode`() {
        val position = Position(
            id = "pos-1",
            instrumentId = "instrument-1",
            instrumentName = "SBER",
            direction = "BUY",
            quantity = 100,
            lotSize = 10,
            entryPrice = 150.0,
            currentPrice = 155.0,
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = "CrossEma",
            aiExplanation = null,
            closeExplanation = null
        )

        val positionCopy = position.copy()
        assertEquals(position.hashCode(), positionCopy.hashCode())
    }

    @Test
    fun `Position must support copy with modified fields`() {
        val position = Position(
            id = "pos-1",
            instrumentId = "instrument-1",
            instrumentName = "SBER",
            direction = "BUY",
            quantity = 100,
            lotSize = 10,
            entryPrice = 150.0,
            currentPrice = 155.0,
            pnl = 500.0,
            pnlPercent = 3.33,
            isClosed = false,
            entryTime = "2026-09-01T10:00:00Z",
            closeTime = null,
            entryStrategyName = "CrossEma",
            aiExplanation = null,
            closeExplanation = null
        )

        val closedPosition = position.copy(
            isClosed = true,
            closeTime = "2026-09-01T15:00:00Z",
            closeExplanation = "Stop loss"
        )

        assertTrue(closedPosition.isClosed)
        assertNotNull(closedPosition.closeTime)
        assertEquals("Stop loss", closedPosition.closeExplanation)
        assertEquals("CrossEma", closedPosition.entryStrategyName)
    }
}
