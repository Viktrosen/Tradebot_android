package ru.bolotov.feature.positions.presentation.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.feature.positions.domain.interactor.PositionsInteractor
import ru.bolotov.feature.positions.domain.model.ClosePositionUnavailableException
import javax.inject.Inject

@Stable
/**
 * MVI-координатор списка позиций, фильтров, ручного закрытия и BottomSheet с
 * объяснением AI либо причиной закрытия.
 */
internal class PositionsViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val positionsInteractor: PositionsInteractor,
    private val tradingStateInteractor: TradingStateInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {

    override val container: Container<UiState, Effect> = container(UiState())
    private var allPositions: List<UiPosition> = emptyList()

    init {
        loadData()
        observeTradingState()
    }

    private fun observeTradingState() {
        viewModelScope.launch {
            tradingStateInteractor.updates().collect(::applyTradingStateUpdate)
        }
    }

    private fun applyTradingStateUpdate(update: TradingStateUpdate) = intent {
        when (update) {
            is TradingStateUpdate.PositionPrice -> {
                allPositions = allPositions.map { position ->
                    if (position.instrumentId == update.instrumentId && !position.isClosed) {
                        position.copy(
                            currentPrice = update.currentPrice,
                            pnl = update.unrealizedPnl,
                            pnlPercent = update.pnlPercent
                        )
                    } else position
                }
                updateState(state.filterType)
            }
            TradingStateUpdate.PositionsChanged -> loadData()
            else -> Unit
        }
    }

    /** Принимает действие UI и изменяет state либо запускает доменную операцию. */
    fun handleAction(action: Action) {
        when (action) {
            Action.LoadData -> loadData()
            Action.Refresh -> refresh()
            is Action.Filter -> applyFilter(action.filterType)
            is Action.ClosePosition -> showCloseConfirmation(action.positionId)
            Action.ConfirmClose -> confirmClose()
            Action.DismissCloseDialog -> dismissCloseDialog()
            is Action.ShowPositionInfo -> showPositionInfo(action.positionId)
            Action.DismissPositionInfo -> dismissPositionInfo()
            is Action.NavigateToInstrument -> navigateToInstrument(action.instrumentId)
        }
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true, errorMessage = null) }
        try {
            allPositions = positionsInteractor.getPositions().map { position ->
                UiPosition(
                    id = position.id,
                    instrumentId = position.instrumentId,
                    instrumentName = position.instrumentName,
                    direction = position.direction,
                    positionSide = position.positionSide,
                    quantity = position.quantity,
                    entryPrice = position.entryPrice,
                    currentPrice = position.currentPrice,
                    pnl = position.pnl,
                    pnlPercent = position.pnlPercent,
                    isClosed = position.isClosed,
                    entryTime = position.entryTime,
                    closeTime = position.closeTime,
                    entryStrategyName = position.entryStrategyName,
                    aiExplanation = position.aiExplanation,
                    closeExplanation = position.closeExplanation,
                    brokerStopLossPrice = position.brokerStopLossPrice,
                    managedExitPrice = position.managedExitPrice,
                    profitProtectionStage = position.profitProtectionStage
                )
            }
            updateState(state.filterType)
            analyticManager.logEvent("positions_loaded")
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false, errorMessage = e.message ?: "Не удалось загрузить позиции") }
            postSideEffect(Effect.ShowError(e.message ?: "Не удалось загрузить позиции"))
        }
    }

    private fun refresh() = intent {
        loadData()
        postSideEffect(Effect.Refresh)
    }

    private fun applyFilter(filterType: PositionFilter) = intent { updateState(filterType) }

    private fun showCloseConfirmation(positionId: String) = intent {
        allPositions.find { it.id == positionId && !it.isClosed }?.let { position ->
            reduce { state.copy(closingPosition = position) }
        }
    }

    private fun confirmClose() = intent {
        val positionId = state.closingPosition?.id ?: return@intent
        reduce { state.copy(isClosingPosition = true) }
        try {
            positionsInteractor.closePosition(positionId)
            reduce { state.copy(isClosingPosition = false, closingPosition = null) }
            loadData()
            postSideEffect(Effect.ShowToast("Позиция закрыта"))
        } catch (e: Exception) {
            if (e is ClosePositionUnavailableException) {
                reduce { state.copy(isClosingPosition = false, closingPosition = null) }
                loadData()
                postSideEffect(Effect.ShowToast("Позиция уже закрыта или недоступна"))
                return@intent
            }
            reduce { state.copy(isClosingPosition = false) }
            postSideEffect(Effect.ShowError(e.message ?: "Не удалось закрыть позицию"))
        }
    }

    private fun dismissCloseDialog() = intent { reduce { state.copy(closingPosition = null) } }

    private fun showPositionInfo(positionId: String) = intent {
        val position = allPositions.firstOrNull { it.id == positionId }
        reduce { state.copy(infoPosition = position?.takeIf { !it.infoExplanation.isNullOrBlank() }) }
    }

    private fun dismissPositionInfo() = intent { reduce { state.copy(infoPosition = null) } }

    private fun navigateToInstrument(instrumentId: String) = intent {
        postSideEffect(Effect.ShowToast("Переход к инструменту $instrumentId"))
    }

    private fun updateState(filterType: PositionFilter) = intent {
        val filteredPositions = when (filterType) {
            PositionFilter.ALL -> allPositions
            PositionFilter.OPEN -> allPositions.filterNot { it.isClosed }
            PositionFilter.PROFIT -> allPositions.filter { it.isProfit }
            PositionFilter.LOSS -> allPositions.filter { it.isLoss }
        }
        reduce {
            state.copy(
                isLoading = false,
                positions = allPositions,
                filteredPositions = filteredPositions,
                filterType = filterType,
                totalPnl = allPositions.filter { it.isClosed }.sumOf { it.pnl ?: 0.0 },
                openPositionsCount = allPositions.count { !it.isClosed },
                closedPositionsCount = allPositions.count { it.isClosed },
                errorMessage = null
            )
        }
    }
}
