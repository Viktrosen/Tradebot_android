package ru.bolotov.feature.strategy.presentation.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.VotingWeights
import javax.inject.Inject

/**
 * MVI-координатор конфигураций стратегий. Перед открытием диалога переносит
 * сохранённые значения в state, а после сохранения перечитывает их с backend.
 */
@Stable
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

        is Action.SaveConfirmation -> saveSettings { confirmation(indicators = action.indicators) }
        is Action.SaveVoting -> saveVotingSettings(weights = action.weights)
        is Action.SetVotingParticipant -> editVotingDraft { withParticipant(participant = action.participant, enabled = action.enabled) }
        is Action.SetVotingWeight -> editVotingDraft { withWeight(participant = action.participant, weight = action.weight) }
        is Action.SaveCandlestick -> saveSettings {
            candlestick(timeframe = action.timeframe, minConfidence = action.minConfidence)
        }
        is Action.OpenConfigDialog -> openDialog(strategyType = action.type)
        Action.DismissDialog -> dismissDialog()
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            val strategies = strategyInteractor.getStrategies()
            reduce { state.withStrategies(strategies).copy(isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val message = error.message.orEmpty()
            reduce { state.copy(isLoading = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun openDialog(strategyType: StrategyType) = intent {
        val dialog = strategyType.configurationDialog ?: return@intent
        reduce {
            state.copy(
                dialog = dialog,
                votingDraft = if (dialog == StrategyDialog.Voting) VotingWeights.from(state.votingWeights) else state.votingDraft
            )
        }
        analyticManager.logEvent("strategy_configuration_opened")
    }

    private fun dismissDialog() = intent {
        reduce { state.copy(dialog = null) }
    }

    private fun editVotingDraft(change: VotingWeights.() -> VotingWeights) = intent {
        if (state.isSaving || state.dialog != StrategyDialog.Voting) return@intent
        reduce { state.copy(votingDraft = state.votingDraft.change()) }
    }

    private fun saveVotingSettings(weights: Map<String, Int>) {
        val configuration = VotingWeights.from(weights)
        if (!configuration.isValid) return
        saveSettings { voting(weights = configuration.toPayload()) }
    }

    private fun saveSettings(operation: suspend StrategyInteractor.() -> Unit) = intent {
        if (state.isSaving) return@intent
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
            postSideEffect(Effect.SettingsSaved)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val message = error.message.orEmpty()
            reduce { state.copy(isSaving = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun UiState.withStrategies(strategies: Strategies): UiState = copy(
        availableStrategies = strategies.items.map { item ->
            StrategyItem(
                name = item.name,
                type = StrategyType.fromCatalog(item.type),
                description = item.description
            )
        },
        confirmationIndicators = strategies.configurations.confirmation.indicators.toSet(),
        votingWeights = strategies.configurations.voting.weights,
        candlestickTimeframe = strategies.configurations.candlestick.timeframe,
        candlestickMinConfidence = strategies.configurations.candlestick.minConfidence,
        errorMessage = null
    )
}
