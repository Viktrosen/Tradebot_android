package ru.bolotov.feature.strategy.presentation.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.StrategySettings
import javax.inject.Inject

@Stable
/**
 * MVI-координатор конфигураций стратегий. Перед открытием диалога переносит
 * сохранённые значения в state, а после сохранения перечитывает их с backend.
 */
internal class StrategyViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val strategyInteractor: StrategyInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {

    override val container: Container<UiState, Effect> = container(UiState())

    init {
        loadData()
    }

    /** Единственная точка входа действий UI: загрузка, диалоги и сохранение конфигураций. */
    fun handleAction(action: Action) = when (action) {
        Action.LoadData,
        Action.Refresh -> loadData()

        is Action.SelectStrategy -> openDialog(action.strategyName, action.type)
        is Action.SwitchToSimple -> switchStrategy { simple(action.name) }
        is Action.SwitchToConfirmation -> switchStrategy { confirmation(action.indicators) }
        is Action.SwitchToVoting -> switchStrategy { voting(action.weights) }
        is Action.SwitchToCandlestick -> switchStrategy {
            candlestick(action.timeframe, action.minConfidence)
        }
        is Action.OpenConfigDialog -> openDialog(action.strategyName, action.type)
        Action.DismissDialog -> dismissDialog()
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            val strategies = strategyInteractor.getStrategies()
            reduce { state.withStrategies(strategies).copy(isLoading = false) }
        } catch (error: Exception) {
            val message = error.message ?: "Не удалось загрузить стратегии"
            reduce { state.copy(isLoading = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun openDialog(strategyName: String, strategyType: StrategyType) = intent {
        val dialog = when (strategyType) {
            StrategyType.SIMPLE -> StrategyDialog.Simple(strategyName)
            StrategyType.CONFIRMATION -> StrategyDialog.Confirmation
            StrategyType.VOTING -> StrategyDialog.Voting
            StrategyType.CANDLESTICK -> StrategyDialog.Candlestick
        }

        reduce { state.copy(dialog = dialog) }

        when (dialog) {
            is StrategyDialog.Simple -> {
                postSideEffect(Effect.ShowSimpleStrategyDialog(dialog.strategyName))
            }

            StrategyDialog.Confirmation -> {
                postSideEffect(Effect.ShowConfirmationDialog(state.confirmationIndicators.toList()))
            }

            StrategyDialog.Voting -> {
                postSideEffect(Effect.ShowVotingDialog(state.votingWeights))
            }

            StrategyDialog.Candlestick -> {
                postSideEffect(
                    Effect.ShowCandlestickDialog(
                        state.candlestickTimeframe,
                        state.candlestickMinConfidence
                    )
                )
            }
        }

        analyticManager.logEvent("strategy_selected")
    }

    private fun dismissDialog() = intent {
        reduce { state.copy(dialog = null) }
    }

    private fun switchStrategy(operation: suspend StrategyInteractor.() -> Unit) = intent {
        reduce { state.copy(isSaving = true, errorMessage = null) }

        try {
            strategyInteractor.operation()
            val strategies = strategyInteractor.getStrategies()

            reduce {
                state.withStrategies(strategies).copy(
                    isSaving = false,
                    dialog = null
                )
            }
            postSideEffect(Effect.StrategyChanged)
            postSideEffect(Effect.ShowToast("Стратегия переключена"))
        } catch (error: Exception) {
            val message = error.message ?: "Не удалось переключить стратегию"
            reduce { state.copy(isSaving = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun String.toStrategyType() = when (lowercase()) {
        "simple" -> StrategyType.SIMPLE
        "confirmation" -> StrategyType.CONFIRMATION
        "voting" -> StrategyType.VOTING
        else -> StrategyType.CANDLESTICK
    }

    private fun StrategySettings.confirmationIndicatorsOr(default: Set<String>): Set<String> = when (this) {
        is StrategySettings.Confirmation -> indicators.toSet()
        else -> default
    }

    private fun StrategySettings.votingWeightsOr(default: Map<String, Int>): Map<String, Int> = when (this) {
        is StrategySettings.Voting -> weights
        else -> default
    }

    private fun StrategySettings.candlestickTimeframeOr(default: String): String = when (this) {
        is StrategySettings.Candlestick -> timeframe
        else -> default
    }

    private fun StrategySettings.candlestickMinConfidenceOr(default: Double): Double = when (this) {
        is StrategySettings.Candlestick -> minConfidence
        else -> default
    }

    private fun UiState.withStrategies(strategies: Strategies): UiState = copy(
        availableStrategies = strategies.items.map { item ->
            StrategyItem(
                name = item.name,
                type = item.type.toStrategyType(),
                description = item.description,
                isCurrent = item.name.equals(strategies.current.id, ignoreCase = true)
            )
        },
        currentStrategy = strategies.current.name,
        currentStrategySettings = strategies.current.settings,
        confirmationIndicators = strategies.configurations.confirmation.indicators.toSet(),
        votingWeights = strategies.configurations.voting.weights,
        candlestickTimeframe = strategies.configurations.candlestick.timeframe,
        candlestickMinConfidence = strategies.configurations.candlestick.minConfidence,
        errorMessage = null
    )
}
