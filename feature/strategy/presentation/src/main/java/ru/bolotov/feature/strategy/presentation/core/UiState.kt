package ru.bolotov.feature.strategy.presentation.core

import androidx.compose.runtime.Stable
import ru.bolotov.feature.strategy.domain.model.StrategySettings

data class UiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val availableStrategies: List<StrategyItem> = emptyList(),
    val currentStrategy: String = "Confirmation (EMA + RSI + MACD + BB)",
    val currentStrategySettings: StrategySettings = StrategySettings.None,
    val dialog: StrategyDialog? = null,
    val confirmationIndicators: Set<String> = setOf("EMA", "RSI", "MACD", "BB"),
    val votingWeights: Map<String, Int> = mapOf("EMA" to 1, "RSI" to 1, "MACD" to 1, "BB" to 1),
    val candlestickTimeframe: String = "M15",
    val candlestickMinConfidence: Double = 0.85,
    val errorMessage: String? = null
)

sealed interface StrategyDialog {
    data class Simple(val strategyName: String) : StrategyDialog
    data object Confirmation : StrategyDialog
    data object Voting : StrategyDialog
    data object Candlestick : StrategyDialog
}

data class StrategyItem(
    val name: String,
    val type: StrategyType,
    val description: String,
    val isCurrent: Boolean = false
)

enum class StrategyType {
    SIMPLE,      // EMA / RSI / MACD по отдельности
    VOTING,      // Взвешенное голосование
    CONFIRMATION,// Стратегия подтверждения
    CANDLESTICK  // Свечные паттерны
}

sealed interface Effect {
    data class ShowToast(val message: String) : Effect
    data class ShowError(val error: String) : Effect
    data object StrategyChanged : Effect
    data class ShowSimpleStrategyDialog(val name: String) : Effect
    data class ShowConfirmationDialog(val indicators: List<String>) : Effect
    data class ShowVotingDialog(val weights: Map<String, Int>) : Effect
    data class ShowCandlestickDialog(val timeframe: String, val minConfidence: Double) : Effect
}

sealed interface Action {
    data object LoadData : Action
    data object Refresh : Action
    data class SelectStrategy(val strategyName: String, val type: StrategyType) : Action

    // Simple Strategy
    data class SwitchToSimple(val name: String) : Action

    // Confirmation Strategy
    data class SwitchToConfirmation(val indicators: List<String>) : Action

    // Voting Strategy
    data class SwitchToVoting(val weights: Map<String, Int>) : Action

    // Candlestick Strategy
    data class SwitchToCandlestick(val timeframe: String, val minConfidence: Double) : Action

    // Config dialogs
    data class OpenConfigDialog(val strategyName: String, val type: StrategyType) : Action
    data object DismissDialog : Action
}
