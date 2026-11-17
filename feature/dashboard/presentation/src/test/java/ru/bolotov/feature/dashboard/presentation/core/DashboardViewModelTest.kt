package ru.bolotov.feature.dashboard.presentation.core

import androidx.lifecycle.ViewModelStore
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.utils.testing.controlOrbit
import ru.bolotov.feature.dashboard.domain.interactor.DashboardInteractor
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.CurrentStrategy
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.DashboardMetrics
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.StrategySettings
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.model.RiskConfig
import ru.bolotov.feature.risk.domain.model.RiskConfigData

/** Tests the production coordinator through actions/flows, not a copy of domain enum logic. */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val dashboard = FakeDashboard()
    private val events = MutableSharedFlow<TradingStateUpdate>()
    private val risk = FakeRisk()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load maps HTTP snapshot and risk profile into state`() = runTest(dispatcher) {
        val model = model()
        val state = model.container.stateFlow.first { it.riskConfig != null }

        assertFalse(state.isLoading)
        assertEquals(listOf("position"), state.positions.map { it.positionId })
        assertEquals(15.0, state.totalPnl, 0.0)
        assertEquals(-2.0, state.dailyPnl, 0.0)
        assertEquals(700.0, state.availableCash, 0.0)
        assertEquals(65.0, state.winRate, 0.0)
        assertEquals(0.02, state.riskConfig!!.config.stopLossPercent, 0.0)
        assertNull(state.errorMessage)
    }

    @Test
    fun `load failure is visible and explicit retry recovers`() = runTest(dispatcher) {
        dashboard.failure = IOException("offline")
        val model = model()
        val failed = model.container.stateFlow.first { it.errorMessage != null }
        assertFalse(failed.isLoading)
        assertEquals("offline", failed.errorMessage)
        assertEquals(Effect.ShowError("offline"), model.container.sideEffectFlow.first())

        dashboard.failure = null
        model.handleAction(Action.LoadData)
        val recovered = model.container.stateFlow.first { it.riskConfig != null && it.errorMessage == null }
        assertFalse(recovered.isLoading)
    }

    @Test
    fun `refresh loads a new snapshot rather than retaining stale metrics`() = runTest(dispatcher) {
        val model = loadedModel()
        dashboard.snapshot = dashboard.snapshot.copy(metrics = dashboard.snapshot.metrics.copy(availableCash = 42.0))
        model.handleAction(Action.Refresh)

        assertEquals(42.0, model.container.stateFlow.first { it.availableCash == 42.0 }.availableCash, 0.0)
        assertEquals(2, dashboard.loads)
    }

    @Test
    fun `start and stop actions invoke domain commands and refresh confirmed status`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.StartBot)
        runCurrent()
        assertEquals(1, dashboard.starts)
        advanceTimeBy(500)
        runCurrent()
        assertTrue(model.container.stateFlow.first { it.botStatus?.running == true }.botStatus!!.running)
        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)

        model.handleAction(Action.StopBot)
        runCurrent()
        assertEquals(1, dashboard.stops)
        advanceTimeBy(500)
        runCurrent()
        assertFalse(model.container.stateFlow.first { it.botStatus?.running == false }.botStatus!!.running)
        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
    }

    @Test
    fun `failed start emits error without inventing a running status`() = runTest(dispatcher) {
        val model = loadedModel()
        dashboard.commandFailure = IOException("start unavailable")
        model.handleAction(Action.StartBot)

        assertEquals(Effect.ShowError("start unavailable"), model.container.sideEffectFlow.first())
        assertFalse(model.container.stateFlow.value.botStatus!!.running)
    }

    @Test
    fun `realtime quote portfolio status and availability enrich current state`() = runTest(dispatcher) {
        val model = loadedModel()
        events.emit(TradingStateUpdate.PositionPrice(
            instrumentId = "instrument", currentPrice = 103.0, unrealizedPnl = 3.0, pnlPercent = 3.0
        ))
        val quoted = model.container.stateFlow.first { it.positions.single().currentPrice == 103.0 }
        assertEquals(3.0, quoted.positions.single().pnl!!, 0.0)
        events.emit(TradingStateUpdate.Portfolio(totalValue = null, availableCash = 99.0, blockedCash = null))
        assertEquals(99.0, model.container.stateFlow.first { it.availableCash == 99.0 }.availableCash, 0.0)
        events.emit(TradingStateUpdate.BotStatus(running = true))
        model.container.stateFlow.first { it.botStatus?.running == true }
        events.emit(TradingStateUpdate.TradingAvailability(instruments = emptyMap(), allTradingUnavailable = true))
        assertTrue(model.container.stateFlow.first { it.botStatus?.allTradingUnavailable == true }.botStatus!!.allTradingUnavailable)
    }

    @Test
    fun `first resume does not duplicate initialization but later resume and reconnect reload`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.ScreenResumed)
        runCurrent()
        assertEquals(1, dashboard.loads)
        model.handleAction(Action.ScreenResumed)
        runCurrent()
        assertEquals(2, dashboard.loads)
        events.emit(TradingStateUpdate.Reconnected)
        runCurrent()
        assertEquals(3, dashboard.loads)
        events.emit(TradingStateUpdate.PositionsChanged)
        runCurrent()
        assertEquals(4, dashboard.loads)
    }

    @Test
    fun `navigation actions produce destination effects without changing portfolio`() = runTest(dispatcher) {
        val model = loadedModel()
        val before = model.container.stateFlow.value
        val cases = listOf(
            Action.NavigateToRisk to Effect.NavigateToRisk,
            Action.NavigateToPositions to Effect.NavigateToPositions,
            Action.NavigateToStrategy to Effect.NavigateToStrategy,
            Action.OpenPositionDetails(positionId = "historic") to Effect.NavigateToPositionDetails(positionId = "historic")
        )
        for ((action, expected) in cases) {
            model.handleAction(action)
            assertEquals(expected, model.container.sideEffectFlow.first())
        }
        assertEquals(before, model.container.stateFlow.value)
    }

    private suspend fun TestScope.model() = DashboardViewModel(
        analyticManager = mock(AnalyticManager::class.java), dashboardInteractor = dashboard,
        riskInteractor = risk, tradingStateInteractor = object : TradingStateInteractor {
            override fun updates() = events
        }
    ).also {
        store.put(key = "dashboard", viewModel = it)
        controlOrbit(it)
    }

    private suspend fun TestScope.loadedModel(): DashboardViewModel = model().also {
        it.container.stateFlow.first { state -> state.riskConfig != null }
        events.subscriptionCount.first { count -> count > 0 }
    }

    private class FakeDashboard : DashboardInteractor {
        var loads = 0
        var starts = 0
        var stops = 0
        var failure: Exception? = null
        var commandFailure: Exception? = null
        var running = false
        var snapshot = Dashboard(
            openPositions = listOf(Position(
                positionId = "position", instrumentId = "instrument", instrumentName = "TEST",
                direction = "BUY", entryPrice = 100.0, quantity = 1, entryTime = null,
                stopLossPrice = null, atr = null, pnl = 1.0, currentPrice = 101.0
            )),
            metrics = DashboardMetrics(realizedPnl = 15.0, dailyPnl = -2.0, winRate = 65.0,
                closedTradesCount = 10, availableCash = 700.0)
        )
        override suspend fun getBotStatus(): BotStatus {
            failure?.let { throw it }
            return BotStatus(running = running,
                currentStrategy = CurrentStrategy(id = "market_regime", name = "Automatic", description = "",
                    type = "adaptive", settings = StrategySettings()), activeInstruments = listOf("instrument"))
        }
        override suspend fun getPositions() = snapshot.openPositions
        override suspend fun getDashboard(): Dashboard { loads++; return snapshot }
        override suspend fun startBot() { commandFailure?.let { throw it }; starts++; running = true }
        override suspend fun stopBot() { commandFailure?.let { throw it }; stops++; running = false }
    }

    private class FakeRisk : RiskInteractor {
        private val profile = RiskConfig(success = true, config = RiskConfigData(
            positionSizePercent = 0.05, stopLossPercent = 0.02, takeProfitPercent = 0.03,
            maxCapitalUsage = 0.8, maxPositions = 5
        ))
        override suspend fun getRiskConfig() = profile
        override suspend fun updateRiskConfig(config: RiskConfig) = config
        override suspend fun resetRiskConfig() = profile
    }
}
