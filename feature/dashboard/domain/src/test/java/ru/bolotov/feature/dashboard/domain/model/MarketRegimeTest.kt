package ru.bolotov.feature.dashboard.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для MarketRegime enum.
 *
 * Проверяют:
 * - корректность label для каждого режима
 * - логику isTradingAllowed()
 * - логику isWarning()
 */
class MarketRegimeTest {

    @Test
    fun `all regimes must have non-empty labels`() {
        MarketRegime.values().forEach { regime ->
            val label = regime.getLabel()
            assertNotNull("Label for $regime must not be null")
            assertTrue("Label for $regime must not be empty", label.isNotBlank())
        }
    }

    @Test
    fun `STRONG_UPTREND label contains emoji`() {
        assertEquals("📈 Сильный рост", MarketRegime.STRONG_UPTREND.getLabel())
    }

    @Test
    fun `STRONG_DOWNTREND label contains emoji`() {
        assertEquals("📉 Сильное падение", MarketRegime.STRONG_DOWNTREND.getLabel())
    }

    @Test
    fun `WEAK_TREND label contains emoji`() {
        assertEquals("📊 Слабый тренд", MarketRegime.WEAK_TREND.getLabel())
    }

    @Test
    fun `FLAT_LOW_VOL label contains emoji`() {
        assertEquals("➡️ Тихий боковик", MarketRegime.FLAT_LOW_VOL.getLabel())
    }

    @Test
    fun `FLAT_HIGH_VOL label contains emoji`() {
        assertEquals("🌊 Волатильный боковик", MarketRegime.FLAT_HIGH_VOL.getLabel())
    }

    @Test
    fun `VOLATILE label contains emoji`() {
        assertEquals("⚡ Волатильность", MarketRegime.VOLATILE.getLabel())
    }

    @Test
    fun `EXTREME_VOLATILE label contains emoji`() {
        assertEquals("🚫 Экстремальная волатильность", MarketRegime.EXTREME_VOLATILE.getLabel())
    }

    @Test
    fun `UNCERTAIN label contains emoji`() {
        assertEquals("❓ Неопределённость", MarketRegime.UNCERTAIN.getLabel())
    }

    @Test
    fun `trading is allowed for trending regimes`() {
        assertTrue(MarketRegime.STRONG_UPTREND.isTradingAllowed())
        assertTrue(MarketRegime.STRONG_DOWNTREND.isTradingAllowed())
        assertTrue(MarketRegime.WEAK_TREND.isTradingAllowed())
    }

    @Test
    fun `trading is allowed for flat regimes`() {
        assertTrue(MarketRegime.FLAT_LOW_VOL.isTradingAllowed())
        assertTrue(MarketRegime.FLAT_HIGH_VOL.isTradingAllowed())
        assertTrue(MarketRegime.VOLATILE.isTradingAllowed())
    }

    @Test
    fun `trading is NOT allowed for EXTREME_VOLATILE`() {
        assertFalse(MarketRegime.EXTREME_VOLATILE.isTradingAllowed())
    }

    @Test
    fun `trading is NOT allowed for UNCERTAIN`() {
        assertFalse(MarketRegime.UNCERTAIN.isTradingAllowed())
    }

    @Test
    fun `only EXTREME_VOLATILE is warning`() {
        assertTrue(MarketRegime.EXTREME_VOLATILE.isWarning())
        assertFalse(MarketRegime.STRONG_UPTREND.isWarning())
        assertFalse(MarketRegime.STRONG_DOWNTREND.isWarning())
        assertFalse(MarketRegime.WEAK_TREND.isWarning())
        assertFalse(MarketRegime.FLAT_LOW_VOL.isWarning())
        assertFalse(MarketRegime.FLAT_HIGH_VOL.isWarning())
        assertFalse(MarketRegime.VOLATILE.isWarning())
        assertFalse(MarketRegime.UNCERTAIN.isWarning())
    }

    @Test
    fun `all 8 regimes must exist`() {
        assertEquals(8, MarketRegime.values().size)
    }
}
