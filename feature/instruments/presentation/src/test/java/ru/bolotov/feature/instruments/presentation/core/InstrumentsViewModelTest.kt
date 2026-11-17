package ru.bolotov.feature.instruments.presentation.core

import androidx.lifecycle.ViewModelStore
import ru.bolotov.core.utils.testing.controlOrbit
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.feature.instruments.domain.interactor.InstrumentsInteractor
import ru.bolotov.feature.instruments.domain.model.InstrumentFilters
import ru.bolotov.feature.instruments.domain.model.TradingInstrument

@OptIn(ExperimentalCoroutinesApi::class)
class InstrumentsViewModelTest {
    @Test
    fun `initial load maps instruments filters and later availability events`() = runTest {
        withModel { model, interactor, updates ->
            assertFalse(model.container.stateFlow.value.isLoading)
            assertEquals(listOf("one", "two"), model.container.stateFlow.value.instruments.map { it.id })
            assertEquals(interactor.filters.maxCount, model.container.stateFlow.value.filters?.maxCount)

            updates.emit(TradingStateUpdate.TradingAvailability(instruments = mapOf("one" to false), allTradingUnavailable = false))
            runCurrent()
            assertFalse(model.container.stateFlow.value.instruments[0].tradingAvailable)
            assertTrue(model.container.stateFlow.value.instruments[1].tradingAvailable)
        }
    }

    @Test
    fun `load failure exposes error and load action recovers authoritative snapshot`() = runTest {
        withModel { model, interactor, _ ->
            interactor.loadFailure = IOException("load failed")
            model.handleAction(Action.Load)
            runCurrent()
            assertEquals("load failed", model.container.stateFlow.value.errorMessage)
            assertFalse(model.container.stateFlow.value.isLoading)
            assertEquals(Effect.ShowError(message = "load failed"), model.container.sideEffectFlow.first())

            interactor.loadFailure = null
            model.handleAction(Action.Load)
            runCurrent()
            assertEquals(null, model.container.stateFlow.value.errorMessage)
            assertEquals(2, model.container.stateFlow.value.instruments.size)
        }
    }

    @Test
    fun `filter dialog actions do not rescan and successful save preserves entered values`() = runTest {
        withModel { model, interactor, _ ->
            model.handleAction(Action.ShowFilters)
            runCurrent()
            assertTrue(model.container.stateFlow.value.showFiltersDialog)
            model.handleAction(Action.DismissFilters)
            runCurrent()
            assertFalse(model.container.stateFlow.value.showFiltersDialog)
            model.handleAction(Action.ShowFilters)
            val edited = InstrumentFiltersUi(minDailyVolume = 500, minVolatility = 0.2, maxVolatility = 3.0, maxCount = 7)
            model.handleAction(Action.SaveFilters(filters = edited))
            runCurrent()

            assertEquals(InstrumentFilters(minDailyVolume = 500, minVolatility = 0.2, maxVolatility = 3.0, maxCount = 7), interactor.savedFilters)
            assertEquals(edited, model.container.stateFlow.value.filters)
            assertFalse(model.container.stateFlow.value.showFiltersDialog)
            assertEquals(0, interactor.rescans)
            assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        }
    }

    @Test
    fun `failed filter save retains previous confirmed filters`() = runTest {
        withModel { model, interactor, _ ->
            val previous = model.container.stateFlow.value.filters
            interactor.saveFailure = IOException("save failed")
            model.handleAction(Action.ShowFilters)
            model.handleAction(Action.SaveFilters(filters = InstrumentFiltersUi(minDailyVolume = 10, minVolatility = 0.1, maxVolatility = 9.0, maxCount = 2)))
            runCurrent()

            assertEquals(previous, model.container.stateFlow.value.filters)
            assertFalse(model.container.stateFlow.value.showFiltersDialog)
            assertEquals(Effect.ShowError(message = "save failed"), model.container.sideEffectFlow.first())
            assertEquals(0, interactor.rescans)
        }
    }

    @Test
    fun `rescan exposes busy state suppresses duplicate action and replaces list once confirmed`() = runTest {
        withModel { model, interactor, _ ->
            val gate = CompletableDeferred<Unit>()
            interactor.rescanGate = gate
            model.handleAction(Action.Rescan)
            runCurrent()
            assertTrue(model.container.stateFlow.value.isRescanning)
            model.handleAction(Action.Rescan)
            runCurrent()
            assertEquals(1, interactor.rescans)
            gate.complete(Unit)
            runCurrent()

            assertFalse(model.container.stateFlow.value.isRescanning)
            assertEquals(listOf("rescanned"), model.container.stateFlow.value.instruments.map { it.id })
            assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        }
    }

    @Test
    fun `failed rescan clears busy state without dropping the current list`() = runTest {
        withModel { model, interactor, _ ->
            val original = model.container.stateFlow.value.instruments
            interactor.rescanFailure = IOException("rescan failed")
            model.handleAction(Action.Rescan)
            runCurrent()

            assertFalse(model.container.stateFlow.value.isRescanning)
            assertEquals(original, model.container.stateFlow.value.instruments)
            assertEquals(Effect.ShowError(message = "rescan failed"), model.container.sideEffectFlow.first())
        }
    }

    private suspend fun TestScope.withModel(
        block: suspend TestScope.(InstrumentsViewModel, FakeInteractor, MutableSharedFlow<TradingStateUpdate>) -> Unit
    ) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val interactor = FakeInteractor()
            val updates = MutableSharedFlow<TradingStateUpdate>(extraBufferCapacity = 8)
            val realtime = object : TradingStateInteractor {
                override fun updates() = updates
            }
            val model = InstrumentsViewModel(instrumentsInteractor = interactor, tradingStateInteractor = realtime)
            store.put("instruments", model)
            controlOrbit(model)
            block(model, interactor, updates)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    private class FakeInteractor : InstrumentsInteractor {
        val filters = InstrumentFilters(minDailyVolume = 100, minVolatility = 0.1, maxVolatility = 2.0, maxCount = 15)
        var loadFailure: Exception? = null
        var saveFailure: Exception? = null
        var rescanFailure: Exception? = null
        var savedFilters: InstrumentFilters? = null
        var rescans = 0
        var rescanGate: CompletableDeferred<Unit>? = null

        override suspend fun getInstruments(): List<TradingInstrument> {
            loadFailure?.let { throw it }
            return listOf(instrument("one"), instrument("two"))
        }

        override suspend fun getFilters() = filters

        override suspend fun updateFilters(filters: InstrumentFilters) {
            saveFailure?.let { throw it }
            savedFilters = filters
        }

        override suspend fun rescanInstruments(): List<TradingInstrument> {
            rescans++
            rescanFailure?.let { throw it }
            rescanGate?.await()
            return listOf(instrument("rescanned"))
        }

        private fun instrument(id: String) = TradingInstrument(id = id, ticker = id.uppercase(), name = "Test $id", tradingAvailable = true)
    }
}
