package ru.bolotov.feature.strategy.presentation.core

import ru.bolotov.feature.strategy.domain.model.VotingWeights

data class UiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val availableStrategies: List<StrategyItem> = emptyList(),
    val dialog: StrategyDialog? = null,
    val confirmationIndicators: Set<String> = setOf("EMA", "RSI", "MACD", "BB"),
    val votingWeights: Map<String, Int> = mapOf("EMA" to 1, "RSI" to 1, "MACD" to 1, "BB" to 1),
    val votingDraft: VotingWeights = VotingWeights.from(emptyMap()),
    val candlestickTimeframe: String = "M15",
    val candlestickMinConfidence: Double = 0.85,
    val errorMessage: String? = null
)

sealed interface Effect {
    data object SettingsSaved : Effect
    data class ShowError(val error: String) : Effect
}

sealed interface Action {
    data object LoadData : Action
    data object Refresh : Action
    data class SaveConfirmation(val indicators: List<String>) : Action
    data class SaveVoting(val weights: Map<String, Int>) : Action
    data class SetVotingParticipant(val participant: String, val enabled: Boolean) : Action
    data class SetVotingWeight(val participant: String, val weight: Int) : Action
    data class SaveCandlestick(val timeframe: String, val minConfidence: Double) : Action

    // Config dialogs
    data class OpenConfigDialog(val type: StrategyType) : Action
    data object DismissDialog : Action
}
