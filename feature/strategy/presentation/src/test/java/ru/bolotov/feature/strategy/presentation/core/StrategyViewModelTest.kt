package ru.bolotov.feature.strategy.presentation.core

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.testing.controlOrbit
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.model.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class StrategyViewModelTest {
    @Test
    fun `initial load publishes server configurations and configurable catalog`() = runTest {
        withModel { fixture ->
            val state = fixture.model.container.stateFlow.value
            assertFalse(state.isLoading)
            assertEquals(listOf(StrategyType.VOTING), state.availableStrategies.map { it.type })
            assertEquals(fixture.interactor.strategies.configurations.voting.weights, state.votingWeights)
            assertEquals("M15", state.candlestickTimeframe)
            assertEquals(1, fixture.interactor.loads)
        }
    }

    @Test
    fun `refresh shows loading while suspended then replaces settings`() = runTest {
        withModel { fixture ->
            val gate = CompletableDeferred<Unit>()
            fixture.interactor.loadGate = gate
            fixture.model.handleAction(Action.Refresh)
            runCurrent()
            assertTrue(fixture.model.container.stateFlow.value.isLoading)
            gate.complete(Unit)
            runCurrent()
            assertFalse(fixture.model.container.stateFlow.value.isLoading)
            assertEquals(2, fixture.interactor.loads)
        }
    }

    @Test
    fun `load failure is visible and retry clears stale error`() = runTest {
        withModel { fixture ->
            fixture.interactor.failure = IllegalStateException("Offline")
            fixture.model.handleAction(Action.LoadData)
            runCurrent()
            assertEquals("Offline", fixture.model.container.stateFlow.value.errorMessage)
            assertEquals(Effect.ShowError("Offline"), fixture.effects.last())
            fixture.interactor.failure = null
            fixture.model.handleAction(Action.Refresh)
            runCurrent()
            assertNull(fixture.model.container.stateFlow.value.errorMessage)
        }
    }

    @Test
    fun `voting actions edit a draft without saving or losing other participants`() = runTest {
        withModel { fixture ->
            fixture.model.handleAction(Action.OpenConfigDialog(type = StrategyType.VOTING))
            runCurrent()
            assertEquals(fixture.interactor.strategies.configurations.voting.weights, fixture.model.container.stateFlow.value.votingDraft.values)
            fixture.model.handleAction(Action.SetVotingWeight(participant = "SUPER_TREND", weight = 10))
            fixture.model.handleAction(Action.SetVotingParticipant(participant = "EMA", enabled = false))
            runCurrent()
            val draft = fixture.model.container.stateFlow.value.votingDraft.values
            assertEquals(mapOf("SUPER_TREND" to 10, "VWAP" to 7), draft)
            assertTrue(fixture.interactor.votingSaves.isEmpty())
            fixture.model.handleAction(Action.DismissDialog)
            runCurrent()
            assertNull(fixture.model.container.stateFlow.value.dialog)
        }
    }

    @Test
    fun `simple catalog entry has no manual selection dialog`() = runTest {
        withModel { fixture ->
            fixture.model.handleAction(Action.OpenConfigDialog(type = StrategyType.SIMPLE))
            runCurrent()
            assertNull(fixture.model.container.stateFlow.value.dialog)
            assertTrue(fixture.interactor.votingSaves.isEmpty())
        }
    }

    @Test
    fun `invalid direct voting action cannot bypass validation`() = runTest {
        withModel { fixture ->
            fixture.model.handleAction(Action.SaveVoting(weights = mapOf("EMA" to 0, "RSI" to 2)))
            runCurrent()
            assertTrue(fixture.interactor.votingSaves.isEmpty())
            assertFalse(fixture.model.container.stateFlow.value.isSaving)
        }
    }

    @Test
    fun `duplicate save while suspended sends one request and successful save reloads`() = runTest {
        withModel { fixture ->
            fixture.model.handleAction(Action.OpenConfigDialog(type = StrategyType.VOTING))
            runCurrent()
            val gate = CompletableDeferred<Unit>()
            fixture.interactor.saveGate = gate
            val weights = mapOf("SUPER_TREND" to 10, "VWAP" to 1)
            fixture.model.handleAction(Action.SaveVoting(weights = weights))
            runCurrent()
            assertTrue(fixture.model.container.stateFlow.value.isSaving)
            fixture.model.handleAction(Action.SaveVoting(weights = weights))
            runCurrent()
            assertEquals(listOf(weights), fixture.interactor.votingSaves)
            gate.complete(Unit)
            runCurrent()
            assertFalse(fixture.model.container.stateFlow.value.isSaving)
            assertNull(fixture.model.container.stateFlow.value.dialog)
            assertEquals(Effect.SettingsSaved, fixture.effects.last())
            assertEquals(2, fixture.interactor.loads)
        }
    }

    @Test
    fun `failed save keeps configuration dialog available for retry`() = runTest {
        withModel { fixture ->
            fixture.model.handleAction(Action.OpenConfigDialog(type = StrategyType.CANDLESTICK))
            runCurrent()
            fixture.interactor.failure = IllegalStateException("Cannot save")
            fixture.model.handleAction(Action.SaveCandlestick(timeframe = "M30", minConfidence = 0.9))
            runCurrent()
            val state = fixture.model.container.stateFlow.value
            assertEquals(StrategyDialog.Candlestick, state.dialog)
            assertFalse(state.isSaving)
            assertEquals("Cannot save", state.errorMessage)
            assertEquals(Effect.ShowError("Cannot save"), fixture.effects.last())
        }
    }

    @Test
    fun `confirmation action forwards the complete selection`() = runTest {
        withModel { fixture ->
            val indicators = listOf("EMA", "RSI", "BB")
            fixture.model.handleAction(Action.SaveConfirmation(indicators = indicators))
            runCurrent()
            assertEquals(indicators, fixture.interactor.confirmationSaved)
            assertEquals(Effect.SettingsSaved, fixture.effects.last())
        }
    }

    private suspend fun TestScope.withModel(block: suspend TestScope.(Fixture) -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val interactor = RecordingInteractor()
            val model = StrategyViewModel(analyticManager = mock(AnalyticManager::class.java), strategyInteractor = interactor)
            store.put("strategy", model)
            controlOrbit(model)
            val effects = mutableListOf<Effect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.container.sideEffectFlow.collect(effects::add) }
            runCurrent()
            block(Fixture(model = model, interactor = interactor, effects = effects))
        } finally {
            store.clear()
            runCurrent()
            Dispatchers.resetMain()
        }
    }

    private data class Fixture(val model: StrategyViewModel, val interactor: RecordingInteractor, val effects: List<Effect>)

    private class RecordingInteractor : StrategyInteractor {
        var loads = 0
        var failure: Exception? = null
        var loadGate: CompletableDeferred<Unit>? = null
        var saveGate: CompletableDeferred<Unit>? = null
        val votingSaves = mutableListOf<Map<String, Int>>()
        var confirmationSaved: List<String>? = null
        val strategies = Strategies(
            items = listOf(Strategy(name = "Voting", type = "voting", description = "Weights")),
            current = CurrentStrategy(id = "market_regime", name = "Auto", type = "adaptive", description = "", settings = StrategySettings.None),
            configurations = StrategyConfigurations(
                voting = StrategySettings.Voting(weights = mapOf("EMA" to 3, "SUPER_TREND" to 8, "VWAP" to 7)),
                candlestick = StrategySettings.Candlestick(timeframe = "M15", minConfidence = 0.9)
            )
        )
        override suspend fun getStrategies(): Strategies {
            loads++
            loadGate?.await()
            failure?.let { throw it }
            return strategies
        }
        override suspend fun voting(weights: Map<String, Int>) {
            votingSaves += weights
            saveGate?.await()
            failure?.let { throw it }
        }
        override suspend fun confirmation(indicators: List<String>) { confirmationSaved = indicators }
        override suspend fun candlestick(timeframe: String, minConfidence: Double) { failure?.let { throw it } }
    }
}
