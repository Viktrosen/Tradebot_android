package ru.bolotov.feature.positions.presentation.core

data class UiState(
    val isLoading: Boolean = false,
    val positions: List<UiPosition> = emptyList(),
    val filteredPositions: List<UiPosition> = emptyList(),
    val filterType: PositionFilter = PositionFilter.ALL,
    val isClosingPosition: Boolean = false,
    val closingPosition: UiPosition? = null,
    val infoPosition: UiPosition? = null,
    val totalPnl: Double = 0.0,
    val openPositionsCount: Int = 0,
    val closedPositionsCount: Int = 0,
    val errorMessage: String? = null
)

enum class PositionFilter {
    ALL,        // Все позиции (открытые + закрытые)
    OPEN,       // Только открытые
    PROFIT,     // Только прибыльные
    LOSS        // Только убыточные
}

// Presentation model для позиции
data class UiPosition(
    val id: String,
    val instrumentId: String,
    val instrumentName: String,
    val direction: String,
    val positionSide: String,
    val quantity: Long,
    val entryPrice: Double,
    val currentPrice: Double?,
    val pnl: Double?,
    val pnlPercent: Double?,
    val isClosed: Boolean,
    val entryTime: String?,
    val closeTime: String?,
    val entryStrategyName: String?,
    val aiExplanation: String?,
    val closeExplanation: String?,
    val brokerStopLossPrice: Double? = null,
    val managedExitPrice: Double? = null,
    val profitProtectionStage: String? = null
) {
    val isProfit: Boolean = (pnl ?: 0.0) > 0
    val isLoss: Boolean = (pnl ?: 0.0) < 0
    val infoExplanation: String? = if (isClosed) closeExplanation else aiExplanation
}

sealed interface Effect {
    data class ShowToast(val message: String) : Effect
    data class ShowError(val error: String) : Effect
    data object Refresh : Effect
}

sealed interface Action {
    data object LoadData : Action
    data object Refresh : Action
    data class Filter(val filterType: PositionFilter) : Action
    data class ClosePosition(val positionId: String) : Action
    data object ConfirmClose : Action
    data object DismissCloseDialog : Action
    data class ShowPositionInfo(val positionId: String) : Action
    data object DismissPositionInfo : Action
    data class NavigateToInstrument(val instrumentId: String) : Action
}
