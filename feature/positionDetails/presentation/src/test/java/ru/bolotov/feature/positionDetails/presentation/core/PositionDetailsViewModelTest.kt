package ru.bolotov.feature.positionDetails.presentation.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.positionDetails.domain.interactor.PositionDetailsInteractor
import ru.bolotov.feature.positionDetails.domain.model.PositionDetails
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate
import ru.bolotov.core.utils.testing.controlOrbit

@OptIn(ExperimentalCoroutinesApi::class)
class PositionDetailsViewModelTest {
    private class FakeInteractor : PositionDetailsInteractor {
        var closes = 0
        var closeFails = false
        var loadFails = false
        var closeGate: kotlinx.coroutines.CompletableDeferred<Unit>? = null
        var details =
            PositionDetails(
                positionId = "position",
                instrumentId = "instrument",
                instrumentName = "Test",
                positionSide = "LONG",
                quantity = 1,
                lotSize = 10,
                entryPrice = 100.0,
            )
        val updates = MutableSharedFlow<PositionDetailsUpdate>()

        override suspend fun getDetails(positionId: String): PositionDetails {
            if (loadFails) throw java.io.IOException("Offline")
            return details
        }

        override fun observeUpdates(positionId: String) = updates

        override suspend fun requestClose(positionId: String) {
            closes++
            closeGate?.await()
            if (closeFails) throw java.io.IOException("Response lost")
        }
    }

    @Test
    fun `expansion actions persist independently in saved state`() = runTest {
        withModel { model, _, saved, _ ->
            model.handleAction(Action.ToggleExplanation)
            model.handleAction(Action.ToggleEntryExplanation)
            runCurrent()
            assertTrue(model.container.stateFlow.value.isExplanationExpanded)
            assertTrue(model.container.stateFlow.value.isEntryExplanationExpanded)
            assertEquals(true, saved.get<Boolean>("positionDetails.explanation"))
            assertEquals(true, saved.get<Boolean>("positionDetails.entryExplanation"))
            model.handleAction(Action.ToggleExplanation)
            runCurrent()
            assertFalse(model.container.stateFlow.value.isExplanationExpanded)
            assertTrue(model.container.stateFlow.value.isEntryExplanationExpanded)
        }
    }

    @Test
    fun `confirm without dialog does not send close and dismiss is non destructive`() = runTest {
        withModel { model, interactor, _, _ ->
            model.handleAction(Action.ConfirmClose)
            runCurrent()
            assertEquals(0, interactor.closes)
            model.handleAction(Action.RequestClose)
            runCurrent()
            assertEquals(CloseUiState.Confirming, model.container.stateFlow.value.closeState)
            model.handleAction(Action.DismissCloseDialog)
            runCurrent()
            assertEquals(CloseUiState.Idle, model.container.stateFlow.value.closeState)
            assertEquals(0, interactor.closes)
        }
    }

    @Test
    fun `duplicate confirmation while request is pending never sends a second close`() = runTest {
        withModel { model, interactor, _, _ ->
            val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
            interactor.closeGate = gate
            model.handleAction(Action.RequestClose)
            runCurrent()
            model.handleAction(Action.ConfirmClose)
            runCurrent()
            assertEquals(CloseUiState.Submitting, model.container.stateFlow.value.closeState)
            model.handleAction(Action.ConfirmClose)
            runCurrent()
            assertEquals(1, interactor.closes)
            interactor.details = interactor.details.copy(isClosed = true)
            gate.complete(Unit)
            runCurrent()
            assertTrue(model.container.stateFlow.value.details!!.isClosed)
            assertEquals(CloseUiState.Idle, model.container.stateFlow.value.closeState)
        }
    }

    @Test
    fun `refresh failure keeps loaded snapshot and emits message instead of empty error screen`() = runTest {
        withModel { model, interactor, _, effects ->
            val snapshot = model.container.stateFlow.value.details
            interactor.loadFails = true
            model.handleAction(Action.Refresh)
            runCurrent()
            assertEquals(snapshot, model.container.stateFlow.value.details)
            assertFalse(model.container.stateFlow.value.error)
            assertFalse(model.container.stateFlow.value.isRefreshing)
            assertTrue(effects.last() is Effect.ShowMessage)
        }
    }

    @Test
    fun `initial failure can be retried by action`() = runTest {
        withModel(initialFailure = true) { model, interactor, _, _ ->
            assertTrue(model.container.stateFlow.value.error)
            assertNull(model.container.stateFlow.value.details)
            interactor.loadFails = false
            model.handleAction(Action.LoadData)
            runCurrent()
            assertFalse(model.container.stateFlow.value.error)
            assertNotNull(model.container.stateFlow.value.details)
        }
    }

    @Test
    fun `confirmed closed lifecycle cannot reopen from an old HTTP snapshot`() = runTest {
        withModel { model, interactor, _, _ ->
            interactor.details = interactor.details.copy(isClosed = true)
            model.handleAction(Action.Refresh)
            runCurrent()
            assertTrue(model.container.stateFlow.value.details!!.isClosed)
            interactor.details = interactor.details.copy(isClosed = false)
            model.handleAction(Action.Refresh)
            runCurrent()
            assertTrue(model.container.stateFlow.value.details!!.isClosed)
        }
    }

    @Test
    fun `back action emits navigation without sending a close command`() = runTest {
        withModel { model, interactor, _, effects ->
            model.handleAction(Action.BackClicked)
            runCurrent()
            assertEquals(Effect.NavigateBack, effects.last())
            assertEquals(0, interactor.closes)
        }
    }

    private suspend fun TestScope.withModel(
        initialFailure: Boolean = false,
        block: suspend TestScope.(PositionDetailsViewModel, FakeInteractor, SavedStateHandle, List<Effect>) -> Unit
    ) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val interactor = FakeInteractor().apply { loadFails = initialFailure }
            val saved = SavedStateHandle()
            val model = PositionDetailsViewModel(positionId = "position", interactor = interactor, savedStateHandle = saved)
            store.put("details", model)
            model.container.stateFlow.first { !it.isLoading }
            controlOrbit(model)
            val effects = mutableListOf<Effect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.container.sideEffectFlow.collect(effects::add) }
            runCurrent()
            block(model, interactor, saved, effects)
        } finally {
            store.clear()
            runCurrent()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `uncertain close is never blindly retried and confirmed history removes the close action`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val store = ViewModelStore()
            try {
                val interactor = FakeInteractor().apply { closeFails = true }
                val saved = SavedStateHandle()
                val model =
                    PositionDetailsViewModel(
                        positionId = "position",
                        interactor = interactor,
                        savedStateHandle = saved,
                    )
                store.put("details", model)
                model.container.stateFlow.first { it.details != null }
                model.handleAction(Action.RequestClose)
                model.container.stateFlow.first { it.closeState == CloseUiState.Confirming }
                model.handleAction(Action.ConfirmClose)
                model.container.stateFlow.first { it.closeState == CloseUiState.VerifyingResult }
                assertEquals(1, interactor.closes)
                assertEquals(
                    CloseUiState.VerifyingResult,
                    model.container.stateFlow.value.closeState,
                )
                assertEquals(true, saved.get<Boolean>("positionDetails.pendingClose"))
                model.handleAction(Action.RequestClose)
                model.handleAction(Action.ConfirmClose)
                runCurrent()
                assertEquals(1, interactor.closes)
                interactor.details = interactor.details.copy(isClosed = true, closePrice = 102.0)
                interactor.updates.emit(PositionDetailsUpdate.RefreshRequired)
                model.container.stateFlow.first { it.details?.isClosed == true }
                assertEquals(true, model.container.stateFlow.value.details?.isClosed)
                assertEquals(false, saved.get<Boolean>("positionDetails.pendingClose"))
                model.handleAction(Action.RequestClose)
                runCurrent()
                assertEquals(CloseUiState.Idle, model.container.stateFlow.value.closeState)
                interactor.updates.emit(
                    PositionDetailsUpdate.Quote(currentPrice = 500.0, pnl = 9.0, pnlPercent = 9.0)
                )
                runCurrent()
                assertNull(model.container.stateFlow.value.details?.currentPrice)
            } finally {
                store.clear()
                Dispatchers.resetMain()
            }
        }
}
