package ru.bolotov.feature.strategy.presentation.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StrategyConfigurationAvailabilityTest {
    @Test
    fun `simple strategies cannot open a selection or configuration dialog`() {
        assertNull(StrategyType.SIMPLE.configurationDialog)
    }

    @Test
    fun `unknown catalog types never become candlestick settings`() {
        assertEquals(StrategyType.SIMPLE, StrategyType.fromCatalog("future_strategy"))
        assertNull(StrategyType.fromCatalog("future_strategy").configurationDialog)
    }

    @Test
    fun `supported algorithms retain their independent parameter dialogs`() {
        assertEquals(StrategyDialog.Voting, StrategyType.fromCatalog(" VOTING ").configurationDialog)
        assertEquals(StrategyDialog.Confirmation, StrategyType.fromCatalog("confirmation").configurationDialog)
        assertEquals(StrategyDialog.Candlestick, StrategyType.fromCatalog("candlestick").configurationDialog)
    }

    @Test
    fun `initial state does not pretend an individual strategy is selected`() {
        val state = UiState()
        assertNull(state.dialog)
        assertEquals(emptyList<StrategyItem>(), state.availableStrategies)
    }
}
