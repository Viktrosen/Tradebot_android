package ru.bolotov.feature.positionDetails.presentation.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.positionDetails.domain.interactor.PositionDetailsInteractor
import ru.bolotov.feature.positionDetails.domain.model.PositionDetails
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate

@OptIn(ExperimentalCoroutinesApi::class)
class PositionDetailsViewModelTest {
    private class FakeInteractor : PositionDetailsInteractor {
        var closes = 0
        var closeFails = false
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

        override suspend fun getDetails(positionId: String) = details

        override fun observeUpdates(positionId: String) = updates

        override suspend fun requestClose(positionId: String) {
            closes++
            if (closeFails) throw java.io.IOException("Response lost")
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
