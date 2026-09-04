package ru.bolotov.feature.strategy.presentation.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Проверяет правило, которым экран стратегий показывает бейдж NEW.
 */
class NewStrategyBadgeTest {

    @Test
    fun `marks SuperTrend as new regardless of case`() {
        assertTrue(isNewStrategy("SuperTrend"))
        assertTrue(isNewStrategy("supertrend"))
        assertTrue(isNewStrategy(" SUPERTREND "))
    }

    @Test
    fun `marks VWAP as new regardless of case`() {
        assertTrue(isNewStrategy("VWAP"))
        assertTrue(isNewStrategy("vwap"))
    }

    @Test
    fun `does not mark established strategies as new`() {
        listOf("Cross EMA", "RSI", "MACD", "Bollinger Bands", "Candlestick", "Voting")
            .forEach { strategyName -> assertFalse(isNewStrategy(strategyName)) }
    }
}
