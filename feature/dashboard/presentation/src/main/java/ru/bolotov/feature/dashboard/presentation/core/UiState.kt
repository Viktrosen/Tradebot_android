package ru.bolotov.feature.dashboard.presentation.core

import androidx.compose.runtime.Stable
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.MarketRegime
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig

@Stable
data class UiState(
    val isLoading: Boolean = false,
    val botStatus: BotStatus? = null,
    val marketRegime: MarketRegime? = null,
    val positions: List<Position> = emptyList(),
    val totalPnl: Double? = null,
    val dailyPnl: Double? = null,
    val availableCash: Double = 0.0,
    val winRate: Double? = null,
    val riskConfig: RiskConfig? = null,
    val errorMessage: String? = null
)

/**
 * Действия из UI
 */
internal sealed interface Action {
    data object LoadData : Action
    data object StartBot : Action
    data object StopBot : Action
    data object Refresh : Action
    data object ScreenResumed : Action
    data class OpenPositionDetails(val positionId: String) : Action
    data object NavigateToRisk : Action
    data object NavigateToPositions : Action
    data object NavigateToStrategy : Action
}

/**
 * Эффекты (одноразовые события)
 */
internal sealed interface Effect {
    data class ShowToast(val message: String) : Effect
    data class NavigateToPositionDetails(val positionId: String) : Effect
    data object NavigateToRisk : Effect
    data object NavigateToPositions : Effect
    data object NavigateToStrategy : Effect
    data class ShowError(val error: String) : Effect
}
