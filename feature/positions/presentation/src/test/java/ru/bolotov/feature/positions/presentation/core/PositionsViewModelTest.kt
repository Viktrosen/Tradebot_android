package ru.bolotov.feature.positions.presentation.core

import androidx.lifecycle.ViewModelStore
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
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
import ru.bolotov.feature.positions.domain.interactor.PositionsInteractor
import ru.bolotov.feature.positions.domain.model.ClosePositionUnavailableException
import ru.bolotov.feature.positions.domain.model.Position

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class PositionsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val interactor = FakePositions()
    private val events = MutableSharedFlow<TradingStateUpdate>()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `load calculates closed pnl and filters actual positions`() = runTest(dispatcher) {
        val model = loadedModel()
        val loaded = model.container.stateFlow.value
        assertFalse(loaded.isLoading)
        assertEquals(1, loaded.openPositionsCount)
        assertEquals(1, loaded.closedPositionsCount)
        assertEquals(-3.0, loaded.totalPnl, 0.0)

        for ((filter, expected) in listOf(
            PositionFilter.OPEN to listOf("open"), PositionFilter.PROFIT to listOf("open"),
            PositionFilter.LOSS to listOf("closed"), PositionFilter.ALL to listOf("open", "closed")
        )) {
            model.handleAction(Action.Filter(filterType = filter))
            assertEquals(expected, model.container.stateFlow.first { it.filterType == filter }.filteredPositions.map { it.id })
        }
    }

    @Test
    fun `failed load exposes error and refresh restores data`() = runTest(dispatcher) {
        interactor.loadFailure = IOException("offline")
        val model = model()
        assertEquals("offline", model.container.stateFlow.first { it.errorMessage != null }.errorMessage)
        assertEquals(Effect.ShowError("offline"), model.container.sideEffectFlow.first())
        interactor.loadFailure = null
        model.handleAction(Action.Refresh)
        assertTrue(model.container.sideEffectFlow.first() is Effect.Refresh)
        val state = model.container.stateFlow.first { it.positions.isNotEmpty() }
        assertNull(state.errorMessage)
    }

    @Test
    fun `info dialog uses lifecycle explanation and dismissal clears state`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.ShowPositionInfo(positionId = "open"))
        assertEquals("Entry reasoning", model.container.stateFlow.first { it.infoPosition != null }.infoPosition!!.infoExplanation)
        model.handleAction(Action.DismissPositionInfo)
        model.container.stateFlow.first { it.infoPosition == null }
        model.handleAction(Action.ShowPositionInfo(positionId = "closed"))
        assertEquals("Stop loss", model.container.stateFlow.first { it.infoPosition != null }.infoPosition!!.infoExplanation)
    }

    @Test
    fun `closing requires an open position and confirmed dialog`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.ConfirmClose)
        model.handleAction(Action.ClosePosition(positionId = "closed"))
        runCurrent()
        assertNull(model.container.stateFlow.value.closingPosition)
        assertTrue(interactor.closedIds.isEmpty())

        model.handleAction(Action.ClosePosition(positionId = "open"))
        model.container.stateFlow.first { it.closingPosition?.id == "open" }
        model.handleAction(Action.DismissCloseDialog)
        model.container.stateFlow.first { it.closingPosition == null }
        assertTrue(interactor.closedIds.isEmpty())
    }

    @Test
    fun `successful close refreshes state and clears confirmation`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.ClosePosition(positionId = "open"))
        model.container.stateFlow.first { it.closingPosition != null }
        model.handleAction(Action.ConfirmClose)
        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        val closed = model.container.stateFlow.first { it.openPositionsCount == 0 }
        assertNull(closed.closingPosition)
        assertFalse(closed.isClosingPosition)
        assertEquals(listOf("open"), interactor.closedIds)
    }

    @Test
    fun `failed close retains confirmation and clears progress for explicit retry`() = runTest(dispatcher) {
        val model = loadedModel()
        interactor.closeFailure = IOException("gateway unavailable")
        model.handleAction(Action.ClosePosition(positionId = "open"))
        model.container.stateFlow.first { it.closingPosition != null }
        model.handleAction(Action.ConfirmClose)

        assertEquals(Effect.ShowError("gateway unavailable"), model.container.sideEffectFlow.first())
        val state = model.container.stateFlow.value
        assertFalse(state.isClosingPosition)
        assertEquals("open", state.closingPosition?.id)
        assertEquals(1, state.openPositionsCount)
    }

    @Test
    fun `already unavailable close clears stale dialog and reloads snapshot`() = runTest(dispatcher) {
        val model = loadedModel()
        interactor.closeFailure = ClosePositionUnavailableException()
        model.handleAction(Action.ClosePosition(positionId = "open"))
        model.container.stateFlow.first { it.closingPosition != null }
        model.handleAction(Action.ConfirmClose)

        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        val state = model.container.stateFlow.first { !it.isClosingPosition && it.closingPosition == null }
        runCurrent()
        assertNull(state.closingPosition)
        assertEquals(2, interactor.loads)
    }

    @Test
    fun `duplicate confirmation cannot submit another close while first request is pending`() = runTest(dispatcher) {
        val model = loadedModel()
        val completion = CompletableDeferred<Unit>()
        interactor.closeCompletion = completion
        model.handleAction(Action.ClosePosition(positionId = "open"))
        model.container.stateFlow.first { it.closingPosition != null }
        model.handleAction(Action.ConfirmClose)
        model.container.stateFlow.first { it.isClosingPosition }
        runCurrent()
        assertEquals(listOf("open"), interactor.closedIds)
        model.handleAction(Action.ConfirmClose)
        runCurrent()
        assertEquals(listOf("open"), interactor.closedIds)
        completion.complete(Unit)
        model.container.stateFlow.first { !it.isClosingPosition && it.closingPosition == null }
    }

    @Test
    fun `realtime quote ignores closed lifecycle and reconnect reloads HTTP snapshot`() = runTest(dispatcher) {
        val model = loadedModel()
        events.emit(TradingStateUpdate.PositionPrice(
            instrumentId = "instrument", currentPrice = 95.0, unrealizedPnl = -5.0, pnlPercent = -5.0
        ))
        val state = model.container.stateFlow.first { it.positions.first().currentPrice == 95.0 }
        assertEquals(-5.0, state.positions.first().pnl!!, 0.0)
        assertEquals(-3.0, state.positions.last().pnl!!, 0.0)
        assertEquals(-3.0, state.totalPnl, 0.0)
        events.emit(TradingStateUpdate.Reconnected)
        runCurrent()
        assertEquals(2, interactor.loads)
        model.handleAction(Action.OpenPositionDetails(positionId = "closed"))
        assertEquals(Effect.NavigateToPositionDetails(positionId = "closed"), model.container.sideEffectFlow.first())
    }

    private suspend fun TestScope.model() = PositionsViewModel(
        analyticManager = mock(AnalyticManager::class.java), positionsInteractor = interactor,
        tradingStateInteractor = object : TradingStateInteractor { override fun updates() = events }
    ).also {
        store.put(key = "positions", viewModel = it)
        controlOrbit(it)
    }

    private suspend fun TestScope.loadedModel(): PositionsViewModel = model().also {
        it.container.stateFlow.first { state -> state.positions.isNotEmpty() }
        events.subscriptionCount.first { count -> count > 0 }
    }

    private class FakePositions : PositionsInteractor {
        var positions = listOf(position(id = "open", closed = false), position(id = "closed", closed = true))
        var loadFailure: Exception? = null
        var closeFailure: Exception? = null
        var closeCompletion: CompletableDeferred<Unit>? = null
        var loads = 0
        val closedIds = mutableListOf<String>()
        override suspend fun getPositions(): List<Position> { loads++; loadFailure?.let { throw it }; return positions }
        override suspend fun closePosition(positionId: String) {
            closedIds += positionId
            closeCompletion?.await()
            closeFailure?.let { throw it }
            positions = positions.map { if (it.id == positionId) it.copy(isClosed = true) else it }
        }

        companion object {
            private fun position(id: String, closed: Boolean) = Position(
                id = id, instrumentId = "instrument", instrumentName = "TEST", direction = "BUY",
                quantity = 1, lotSize = 10, entryPrice = 100.0, currentPrice = 102.0,
                pnl = if (closed) -3.0 else 2.0, pnlPercent = if (closed) -3.0 else 2.0,
                isClosed = closed, entryTime = null, closeTime = null, entryStrategyName = "EMA",
                aiExplanation = "Entry reasoning", closeExplanation = if (closed) "Stop loss" else null
            )
        }
    }
}
