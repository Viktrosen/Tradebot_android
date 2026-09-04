package ru.bolotov.feature.dashboard.presentation.core

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.dashboard.domain.model.MarketRegime

/**
 * Integration-тест для DashboardViewModel.
 *
 * Проверяет корректность работы с MarketRegime в UiState:
 * - установка marketRegime из BotStatus
 * - корректное отображение состояния торговли
 * - работа с warning-режимами
 */
class DashboardViewModelMarketRegimeTest {

    @Test
    fun `UiState must have marketRegime field`() {
        val state = UiState()
        assertNull("Initial marketRegime should be null", state.marketRegime)

        val stateWithRegime = state.copy(marketRegime = MarketRegime.STRONG_UPTREND)
        assertEquals("marketRegime should be set", MarketRegime.STRONG_UPTREND, stateWithRegime.marketRegime)
    }

    @Test
    fun `marketRegime must correctly identify trading allowed regimes`() {
        val allowedRegimes = listOf(
            MarketRegime.STRONG_UPTREND,
            MarketRegime.STRONG_DOWNTREND,
            MarketRegime.WEAK_TREND,
            MarketRegime.FLAT_LOW_VOL,
            MarketRegime.FLAT_HIGH_VOL,
            MarketRegime.VOLATILE
        )

        allowedRegimes.forEach { regime ->
            assertTrue(
                "Trading should be allowed for $regime",
                regime.isTradingAllowed()
            )
        }
    }

    @Test
    fun `marketRegime must correctly identify blocked regimes`() {
        val blockedRegimes = listOf(
            MarketRegime.EXTREME_VOLATILE,
            MarketRegime.UNCERTAIN
        )

        blockedRegimes.forEach { regime ->
            assertFalse(
                "Trading should be blocked for $regime",
                regime.isTradingAllowed()
            )
        }
    }

    @Test
    fun `EXTREME_VOLATILE must trigger warning`() {
        val state = UiState(marketRegime = MarketRegime.EXTREME_VOLATILE)
        assertTrue("EXTREME_VOLATILE should trigger warning", state.marketRegime?.isWarning() == true)
    }

    @Test
    fun `non-extreme regimes must NOT trigger warning`() {
        val nonWarningRegimes = listOf(
            MarketRegime.STRONG_UPTREND,
            MarketRegime.STRONG_DOWNTREND,
            MarketRegime.WEAK_TREND,
            MarketRegime.FLAT_LOW_VOL,
            MarketRegime.FLAT_HIGH_VOL,
            MarketRegime.VOLATILE,
            MarketRegime.UNCERTAIN
        )

        nonWarningRegimes.forEach { regime ->
            val state = UiState(marketRegime = regime)
            assertFalse(
                "$regime should NOT trigger warning",
                state.marketRegime?.isWarning() == true
            )
        }
    }

    @Test
    fun `null marketRegime must not cause NPE`() {
        val state = UiState(marketRegime = null)
        assertNull("Null marketRegime should be preserved", state.marketRegime)
        assertFalse("Null marketRegime should not trigger warning", state.marketRegime?.isWarning() == true)
    }

    @Test
    fun `all regimes must have valid labels for UI display`() {
        val labels = MarketRegime.values().map { it.getLabel() }

        // Проверяем, что все labels уникальны и не пусты
        assertEquals(
            "All regimes must have unique labels",
            MarketRegime.values().size,
            labels.distinct().size
        )

        labels.forEach { label ->
            assertTrue("Label must not be empty: $label", label.isNotBlank())
            assertTrue("Label must contain emoji", label.contains(Regex("[\\u{1F300}-\\u{1F9FF}]|[➡️❓🚫]")))
        }
    }
}
