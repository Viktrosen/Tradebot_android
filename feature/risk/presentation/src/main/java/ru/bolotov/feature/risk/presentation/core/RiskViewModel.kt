package ru.bolotov.feature.risk.presentation.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.model.RiskConfig
import ru.bolotov.feature.risk.domain.model.RiskConfigData
import javax.inject.Inject

@Stable
/**
 * MVI-координатор настройки рисков. Хранит редактируемый профиль и состояния
 * подтверждений, чтобы Compose-диалоги не имели собственного состояния.
 */
internal class RiskViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val riskInteractor: RiskInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {

    override val container: Container<UiState, Effect> = container(UiState())

    init {
        loadData()
    }

    /** Обрабатывает изменение полей, подтверждения и команды сохранения/сброса. */
    fun handleAction(action: Action) {
        when (action) {
            Action.LoadData,
            Action.Refresh -> loadData()

            is Action.UpdatePositionSizePercent -> updateConfig {
                copy(positionSizePercent = action.value)
            }

            is Action.UpdateStopLossPercent -> updateConfig {
                copy(stopLossPercent = action.value)
            }

            is Action.UpdateTakeProfitPercent -> updateConfig {
                copy(takeProfitPercent = action.value)
            }

            is Action.UpdateMaxCapitalUsage -> updateConfig {
                copy(maxCapitalUsage = action.value)
            }

            is Action.UpdateMaxPositions -> updateConfig {
                copy(maxPositions = action.value)
            }

            is Action.RequestShortTradingEnabled -> requestShortTradingChange(action.value)
            Action.ConfirmShortTradingEnabled -> confirmShortTradingChange()
            Action.DismissShortTradingConfirmation -> dismissShortTradingConfirmation()

            Action.Save -> save()
            Action.ConfirmReset -> reset()
            Action.ShowResetConfirmation -> showResetConfirmation()
            Action.DismissResetConfirmation -> dismissResetConfirmation()
        }
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true, errorMessage = null) }
        try {
            val config = riskInteractor.getRiskConfig().config.toUi()
            reduce { state.copy(isLoading = false, riskConfig = config) }
            analyticManager.logEvent("risk_config_loaded")
        } catch (error: Exception) {
            val message = error.message ?: "Не удалось загрузить настройки риска"
            reduce { state.copy(isLoading = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun updateConfig(transform: RiskConfigUi.() -> RiskConfigUi) = intent {
        val updatedConfig = state.riskConfig?.transform() ?: return@intent
        reduce { state.copy(riskConfig = updatedConfig) }
    }

    private fun requestShortTradingChange(enabled: Boolean) = intent {
        if (!enabled) {
            reduce { state.copy(riskConfig = state.riskConfig?.copy(shortTradingEnabled = false)) }
            return@intent
        }
        reduce { state.copy(isShortTradingConfirmationVisible = true) }
    }

    private fun confirmShortTradingChange() = intent {
        reduce {
            state.copy(
                riskConfig = state.riskConfig?.copy(shortTradingEnabled = true),
                isShortTradingConfirmationVisible = false
            )
        }
    }

    private fun dismissShortTradingConfirmation() = intent {
        reduce { state.copy(isShortTradingConfirmationVisible = false) }
    }

    private fun save() = intent {
        val config = state.riskConfig ?: return@intent
        reduce { state.copy(isSaving = true, errorMessage = null) }
        try {
            val savedConfig = riskInteractor.updateRiskConfig(config.toDomain()).config.toUi()
            reduce {
                state.copy(
                    isSaving = false,
                    riskConfig = savedConfig,
                    successMessage = "Настройки риска сохранены"
                )
            }
            analyticManager.logEvent("risk_config_saved")
            postSideEffect(Effect.ShowToast("Настройки риска сохранены"))
        } catch (error: Exception) {
            val message = error.message ?: "Не удалось сохранить настройки риска"
            reduce { state.copy(isSaving = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun reset() = intent {
        reduce { state.copy(isResetting = true, errorMessage = null) }
        try {
            val resetConfig = riskInteractor.resetRiskConfig().config.toUi()
            reduce { state.copy(isResetting = false, riskConfig = resetConfig) }
            postSideEffect(Effect.DismissResetConfirmation)
            postSideEffect(Effect.ShowToast("Настройки риска сброшены"))
        } catch (error: Exception) {
            val message = error.message ?: "Не удалось сбросить настройки риска"
            reduce { state.copy(isResetting = false, errorMessage = message) }
            postSideEffect(Effect.ShowError(message))
        }
    }

    private fun showResetConfirmation() = intent {
        postSideEffect(Effect.ShowResetConfirmation)
    }

    private fun dismissResetConfirmation() = intent {
        postSideEffect(Effect.DismissResetConfirmation)
    }

    private fun RiskConfigData.toUi() = RiskConfigUi(
        positionSizePercent = positionSizePercent,
        stopLossPercent = stopLossPercent,
        takeProfitPercent = takeProfitPercent,
        maxCapitalUsage = maxCapitalUsage,
        maxPositions = maxPositions,
        shortTradingEnabled = shortTradingEnabled
    )

    private fun RiskConfigUi.toDomain() = RiskConfig(
        success = true,
        config = RiskConfigData(
            positionSizePercent = positionSizePercent,
            positionSizePercentDisplay = "",
            stopLossPercent = stopLossPercent,
            stopLossPercentDisplay = "",
            takeProfitPercent = takeProfitPercent,
            takeProfitPercentDisplay = "",
            maxCapitalUsage = maxCapitalUsage,
            maxCapitalUsagePercent = "",
            maxPositions = maxPositions,
            shortTradingEnabled = shortTradingEnabled
        )
    )
}
