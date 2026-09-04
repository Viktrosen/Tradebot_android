package ru.bolotov.feature.risk.presentation.core

import androidx.compose.runtime.Stable

data class UiState(
    val isLoading: Boolean = false,
    val riskConfig: RiskConfigUi? = null,
    val isSaving: Boolean = false,
    val isResetting: Boolean = false,
    val isShortTradingConfirmationVisible: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class RiskConfigUi(
    val positionSizePercent: Double = 0.05,
    val stopLossPercent: Double = 0.02,
    val takeProfitPercent: Double = 0.03,
    val maxCapitalUsage: Double = 0.80,
    val maxPositions: Int = 10,
    val shortTradingEnabled: Boolean = false
) {
    val positionSizePercentDisplay: String = "${(positionSizePercent * 100).toInt()}%"
    val stopLossPercentDisplay: String = "${(stopLossPercent * 100).toInt()}%"
    val takeProfitPercentDisplay: String = "${(takeProfitPercent * 100).toInt()}%"
    val maxCapitalUsagePercent: String = "${(maxCapitalUsage * 100).toInt()}%"
}

data class RiskConfigRequest(
    val positionSizePercent: Double? = null,
    val stopLossPercent: Double? = null,
    val takeProfitPercent: Double? = null,
    val maxCapitalUsage: Double? = null,
    val maxPositions: Int? = null
)

sealed interface Effect {
    data class ShowToast(val message: String) : Effect
    data class ShowError(val error: String) : Effect
    data object ShowResetConfirmation : Effect
    data object DismissResetConfirmation : Effect
    data object NavigateBack : Effect
}

sealed interface Action {
    data object LoadData : Action
    data object Refresh : Action
    data class UpdatePositionSizePercent(val value: Double) : Action
    data class UpdateStopLossPercent(val value: Double) : Action
    data class UpdateTakeProfitPercent(val value: Double) : Action
    data class UpdateMaxCapitalUsage(val value: Double) : Action
    data class UpdateMaxPositions(val value: Int) : Action
    data class RequestShortTradingEnabled(val value: Boolean) : Action
    data object ConfirmShortTradingEnabled : Action
    data object DismissShortTradingConfirmation : Action
    data object Save : Action
    data object ShowResetConfirmation : Action
    data object ConfirmReset : Action
    data object DismissResetConfirmation : Action
}
