package ru.bolotov.feature.positionDetails.presentation.core

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.feature.positionDetails.domain.interactor.PositionDetailsInteractor
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate
import ru.bolotov.feature.positionDetails.presentation.mapper.toUi
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

internal class PositionDetailsViewModel
@Inject
constructor(
    private val positionId: String,
    private val interactor: PositionDetailsInteractor,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), ContainerHost<UiState, Effect> {
    override val container =
        container<UiState, Effect>(
            initialState =
                UiState(
                    isExplanationExpanded = savedStateHandle[EXPLANATION] ?: false,
                    isEntryExplanationExpanded = savedStateHandle[ENTRY_EXPLANATION] ?: false,
                    closeState =
                        if (savedStateHandle.get<Boolean>(PENDING_CLOSE) == true) {
                            CloseUiState.VerifyingResult
                        } else CloseUiState.Idle,
                )
        )

    private var loadJob: Job? = null
    private var verificationJob: Job? = null
    private var latestQuote: PositionDetailsUpdate.Quote? = null

    init {
        viewModelScope.launch {
            interactor.observeUpdates(positionId).collect { update ->
                when (update) {
                    is PositionDetailsUpdate.Quote -> {
                        latestQuote = update
                        applyQuote(update)
                    }
                    PositionDetailsUpdate.RefreshRequired -> loadDetails()
                }
            }
        }
        loadDetails()
        if (savedStateHandle.get<Boolean>(PENDING_CLOSE) == true) verifyClose()
    }

    fun handleAction(action: Action) {
        when (action) {
            Action.LoadData,
            Action.Refresh,
            Action.ScreenResumed -> loadDetails()
            Action.ToggleExplanation -> toggleExplanation(entry = false)
            Action.ToggleEntryExplanation -> toggleExplanation(entry = true)
            Action.RequestClose -> requestClose()
            Action.ConfirmClose -> confirmClose()
            Action.DismissCloseDialog -> dismissConfirmation()
            Action.BackClicked -> intent { postSideEffect(Effect.NavigateBack) }
        }
    }

    private fun loadDetails() {
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                intent { reduce { state.copy(isRefreshing = state.details != null) } }.join()
                try {
                    val quoteBeforeRequest = latestQuote
                    val snapshot = interactor.getDetails(positionId).toUi()
                    val newerQuote = latestQuote.takeUnless { it === quoteBeforeRequest }
                    ensureActive()
                    intent {
                            // Once confirmed closed, an older HTTP response cannot reopen this
                            // lifecycle.
                            if (state.details?.isClosed == true && !snapshot.isClosed) return@intent
                            val details =
                                if (snapshot.isClosed) snapshot else snapshot.withQuote(newerQuote)
                            if (details.isClosed) savedStateHandle[PENDING_CLOSE] = false
                            reduce {
                                state.copy(
                                    details = details,
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = false,
                                    closeState =
                                        if (details.isClosed) CloseUiState.Idle
                                        else state.closeState,
                                )
                            }
                        }
                        .join()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    intent {
                            reduce {
                                state.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = state.details == null,
                                )
                            }
                            if (state.details != null)
                                postSideEffect(Effect.ShowMessage(R.string.details_refresh_failed))
                        }
                        .join()
                }
            }
    }

    private fun applyQuote(quote: PositionDetailsUpdate.Quote) = intent {
        val details = state.details?.takeUnless { it.isClosed } ?: return@intent
        reduce { state.copy(details = details.withQuote(quote)) }
    }

    private fun PositionDetailsUiModel.withQuote(quote: PositionDetailsUpdate.Quote?) =
        if (quote == null) this
        else copy(currentPrice = quote.currentPrice, pnl = quote.pnl, pnlPercent = quote.pnlPercent)

    private fun toggleExplanation(entry: Boolean) = intent {
        if (entry) {
            val expanded = !state.isEntryExplanationExpanded
            savedStateHandle[ENTRY_EXPLANATION] = expanded
            reduce { state.copy(isEntryExplanationExpanded = expanded) }
        } else {
            val expanded = !state.isExplanationExpanded
            savedStateHandle[EXPLANATION] = expanded
            reduce { state.copy(isExplanationExpanded = expanded) }
        }
    }

    private fun requestClose() = intent {
        if (state.details?.isClosed == false && state.closeState == CloseUiState.Idle) {
            reduce { state.copy(closeState = CloseUiState.Confirming) }
        }
    }

    private fun dismissConfirmation() = intent {
        if (state.closeState == CloseUiState.Confirming)
            reduce { state.copy(closeState = CloseUiState.Idle) }
    }

    private fun confirmClose() = intent {
        if (state.closeState != CloseUiState.Confirming || state.details?.isClosed != false)
            return@intent
        savedStateHandle[PENDING_CLOSE] = true
        reduce { state.copy(closeState = CloseUiState.Submitting) }
        try {
            interactor.requestClose(positionId)
            reduce { state.copy(closeState = CloseUiState.AwaitingConfirmation) }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // A lost HTTP response can hide a broker fill. Do not make the command retryable.
            reduce { state.copy(closeState = CloseUiState.VerifyingResult) }
            postSideEffect(Effect.ShowMessage(R.string.details_close_unknown))
        }
        loadDetails()
        verifyClose()
    }

    private fun verifyClose() {
        if (verificationJob?.isActive == true) return
        verificationJob =
            viewModelScope.launch {
                repeat(10) {
                    if (container.stateFlow.value.details?.isClosed == true) return@launch
                    delay(2_000)
                    loadDetails()
                    loadJob?.join()
                }
                // Remain pending rather than claiming failure or resending a trading command.
            }
    }

    private companion object {
        const val EXPLANATION = "positionDetails.explanation"
        const val ENTRY_EXPLANATION = "positionDetails.entryExplanation"
        const val PENDING_CLOSE = "positionDetails.pendingClose"
    }
}
