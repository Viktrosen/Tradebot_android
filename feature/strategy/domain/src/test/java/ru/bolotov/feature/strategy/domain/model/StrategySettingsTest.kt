package ru.bolotov.feature.strategy.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для StrategySettings sealed interface.
 *
 * Проверяют:
 * - все типы settings (None, Candlestick, Confirmation, Voting)
 * - корректность полей каждого типа
 * - sealed interface иерархию
 */
class StrategySettingsTest {

    @Test
    fun `StrategySettings.None must be singleton`() {
        val none1 = StrategySettings.None
        val none2 = StrategySettings.None

        assertSame(none1, none2)
        assertEquals(StrategySettings.None, StrategySettings.None)
    }

    @Test
    fun `StrategySettings.Candlestick must have timeframe and minConfidence`() {
        val candlestick = StrategySettings.Candlestick("M30", 0.90)

        assertEquals("M30", candlestick.timeframe)
        assertEquals(0.90, candlestick.minConfidence, 0.001)
    }

    @Test
    fun `StrategySettings.Candlestick must support default values`() {
        val candlestick = StrategySettings.Candlestick("M5", 0.85)

        assertEquals("M5", candlestick.timeframe)
        assertEquals(0.85, candlestick.minConfidence, 0.001)
    }

    @Test
    fun `StrategySettings.Candlestick must support copy`() {
        val candlestick = StrategySettings.Candlestick("M5", 0.85)
        val updated = candlestick.copy(timeframe = "H1", minConfidence = 0.95)

        assertEquals("H1", updated.timeframe)
        assertEquals(0.95, updated.minConfidence, 0.001)
        assertEquals(candlestick.minConfidence, candlestick.minConfidence)
    }

    @Test
    fun `StrategySettings.Confirmation must have indicators list`() {
        val confirmation = StrategySettings.Confirmation(listOf("EMA", "RSI", "MACD"))

        assertEquals(3, confirmation.indicators.size)
        assertTrue(confirmation.indicators.contains("EMA"))
        assertTrue(confirmation.indicators.contains("RSI"))
        assertTrue(confirmation.indicators.contains("MACD"))
    }

    @Test
    fun `StrategySettings.Confirmation must support empty list`() {
        val confirmation = StrategySettings.Confirmation(emptyList())

        assertTrue(confirmation.indicators.isEmpty())
        assertEquals(0, confirmation.indicators.size)
    }

    @Test
    fun `StrategySettings.Confirmation must support copy`() {
        val confirmation = StrategySettings.Confirmation(listOf("EMA", "RSI"))
        val updated = confirmation.copy(indicators = listOf("EMA", "MACD", "BB"))

        assertEquals(3, updated.indicators.size)
        assertTrue(updated.indicators.contains("BB"))
    }

    @Test
    fun `StrategySettings.Voting must have weights map`() {
        val voting = StrategySettings.Voting(mapOf("EMA" to 3, "RSI" to 2, "MACD" to 2, "BB" to 2))

        assertEquals(4, voting.weights.size)
        assertEquals(3, voting.weights["EMA"])
        assertEquals(2, voting.weights["RSI"])
    }

    @Test
    fun `StrategySettings.Voting must support empty weights`() {
        val voting = StrategySettings.Voting(emptyMap())

        assertTrue(voting.weights.isEmpty())
        assertEquals(0, voting.weights.size)
    }

    @Test
    fun `StrategySettings.Voting must support copy`() {
        val voting = StrategySettings.Voting(mapOf("EMA" to 3, "RSI" to 2))
        val updated = voting.copy(weights = mapOf("EMA" to 5, "RSI" to 3, "MACD" to 3))

        assertEquals(3, updated.weights.size)
        assertEquals(5, updated.weights["EMA"])
    }

    @Test
    fun `All StrategySettings types must implement StrategySettings interface`() {
        val none: StrategySettings = StrategySettings.None
        val candlestick: StrategySettings = StrategySettings.Candlestick("M5", 0.85)
        val confirmation: StrategySettings = StrategySettings.Confirmation(emptyList())
        val voting: StrategySettings = StrategySettings.Voting(emptyMap())

        assertTrue(none is StrategySettings)
        assertTrue(candlestick is StrategySettings)
        assertTrue(confirmation is StrategySettings)
        assertTrue(voting is StrategySettings)
    }

    @Test
    fun `StrategySettings types must be distinguishable by type check`() {
        val settings = listOf(
            StrategySettings.None to "None",
            StrategySettings.Candlestick("M5", 0.85) to "Candlestick",
            StrategySettings.Confirmation(emptyList()) to "Confirmation",
            StrategySettings.Voting(emptyMap()) to "Voting"
        )

        settings.forEach { (setting, expectedType) ->
            when (setting) {
                is StrategySettings.None -> assertEquals("None", expectedType)
                is StrategySettings.Candlestick -> assertEquals("Candlestick", expectedType)
                is StrategySettings.Confirmation -> assertEquals("Confirmation", expectedType)
                is StrategySettings.Voting -> assertEquals("Voting", expectedType)
            }
        }
    }

    @Test
    fun `StrategySettings must support when expression with exhaustive check`() {
        val settings = listOf(
            StrategySettings.None,
            StrategySettings.Candlestick("M30", 0.90),
            StrategySettings.Confirmation(listOf("EMA", "RSI")),
            StrategySettings.Voting(mapOf("EMA" to 3, "RSI" to 2))
        )

        val types = settings.map { setting ->
            when (setting) {
                is StrategySettings.None -> "None"
                is StrategySettings.Candlestick -> "Candlestick"
                is StrategySettings.Confirmation -> "Confirmation"
                is StrategySettings.Voting -> "Voting"
            }
        }

        assertEquals(4, types.size)
        assertTrue(types.contains("None"))
        assertTrue(types.contains("Candlestick"))
        assertTrue(types.contains("Confirmation"))
        assertTrue(types.contains("Voting"))
    }
}
