package ru.bolotov.feature.dashboard.presentation.core

import androidx.compose.runtime.Stable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost

import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.feature.dashboard.domain.interactor.DashboardInteractor
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.MarketRegime
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.model.RiskConfig as DomainRiskConfig
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.model.RiskConfigData
import javax.inject.Inject

@Stable
/**
 * MVI-координатор Dashboard: объединяет начальную HTTP-загрузку с WebSocket
 * обновлениями цен, портфеля, позиций и статуса бота.
 */
internal class DashboardViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val dashboardInteractor: DashboardInteractor,
    private val riskInteractor: RiskInteractor,
    private val tradingStateInteractor: TradingStateInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {
    override val container: Container<UiState, Effect> = container(UiState())
    private var hasReceivedFirstResume = false

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
            is TradingStateUpdate.PositionPrice -> reduce {
                state.copy(positions = state.positions.map { position ->
                    if (position.instrumentId == update.instrumentId) {
                        position.copy(
                            currentPrice = update.currentPrice,
                            pnl = update.unrealizedPnl
                        )
                    } else position
                })
            }
            is TradingStateUpdate.Portfolio -> reduce {
                state.copy(availableCash = update.availableCash ?: state.availableCash)
            }
            is TradingStateUpdate.BotStatus -> reduce {
                state.copy(botStatus = state.botStatus?.copy(running = update.running))
            }
            is TradingStateUpdate.TradingAvailability -> reduce {
                state.copy(
                    botStatus = state.botStatus?.copy(
                        allTradingUnavailable = update.allTradingUnavailable
                    )
                )
            }
            TradingStateUpdate.PositionsChanged -> loadData()
        }
    }

    /** Принимает все пользовательские действия экрана и направляет их в MVI intent. */
    fun handleAction(action: Action) {
        when (action) {
            Action.LoadData -> loadData()
            Action.StartBot -> startBot()
            Action.StopBot -> stopBot()
            Action.Refresh -> refresh()
            Action.ScreenResumed -> refreshOnScreenResume()
            Action.NavigateToRisk -> navigateToRisk()
            Action.NavigateToPositions -> navigateToPositions()
            Action.NavigateToStrategy -> navigateToStrategy()
        }
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            val botStatus = dashboardInteractor.getBotStatus()
            val dashboard = dashboardInteractor.getDashboard()
            val riskConfig = riskInteractor.getRiskConfig().toDashboardModel()

            reduce {
                state.copy(
                    isLoading = false,
                    botStatus = botStatus,
                    marketRegime = botStatus.marketRegime?.let { MarketRegime.valueOf(it) },  // НОВОЕ
                    positions = dashboard.openPositions,
                    totalPnl = dashboard.metrics.realizedPnl,
                    dailyPnl = dashboard.metrics.dailyPnl,
                    availableCash = dashboard.metrics.availableCash,
                    winRate = dashboard.metrics.winRate,
                    riskConfig = riskConfig,
                    errorMessage = null
                )
            }

            analyticManager.logEvent("dashboard_loaded", bundleOf(
                "positions_count" to dashboard.openPositions.size,
                "is_running" to botStatus.running
            )
            )

        } catch (e: Exception) {
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Ошибка загрузки данных"
                )
            }
            postSideEffect(Effect.ShowError(e.message ?: "Ошибка загрузки данных"))
        }
    }

    private fun startBot() = intent {
        try {
            dashboardInteractor.startBot()
            // Ждём немного, чтобы бот успел обновить статус
            delay(500)
            refresh()
            postSideEffect(Effect.ShowToast("Бот запущен"))
            analyticManager.logEvent("bot_started")
        } catch (e: Exception) {
            postSideEffect(Effect.ShowError(e.message ?: "Ошибка запуска бота"))
        }
    }

    private fun stopBot() = intent {
        try {
            dashboardInteractor.stopBot()
            delay(500)
            refresh()
            postSideEffect(Effect.ShowToast("Бот остановлен"))
            analyticManager.logEvent("bot_stopped")
        } catch (e: Exception) {
            postSideEffect(Effect.ShowError(e.message ?: "Ошибка остановки бота"))
        }
    }

    private fun refresh() = intent {
        loadData()
        analyticManager.logEvent("dashboard_refreshed")
    }

    private fun refreshOnScreenResume() {
        if (hasReceivedFirstResume) {
            loadData()
        } else {
            hasReceivedFirstResume = true
        }
    }

    private fun navigateToRisk() = intent {
        postSideEffect(Effect.NavigateToRisk)
    }

    private fun navigateToPositions() = intent {
        postSideEffect(Effect.NavigateToPositions)
    }

    private fun navigateToStrategy() = intent {
        postSideEffect(Effect.NavigateToStrategy)
    }

    private fun DomainRiskConfig.toDashboardModel() = RiskConfig(
        success = success,
        config = RiskConfigData(
            positionSizePercent = config.positionSizePercent,
            positionSizePercentDisplay = config.positionSizePercentDisplay,
            stopLossPercent = config.stopLossPercent,
            stopLossPercentDisplay = config.stopLossPercentDisplay,
            takeProfitPercent = config.takeProfitPercent,
            takeProfitPercentDisplay = config.takeProfitPercentDisplay,
            maxCapitalUsage = config.maxCapitalUsage,
            maxCapitalUsagePercent = config.maxCapitalUsagePercent,
            maxPositions = config.maxPositions,
            shortTradingEnabled = config.shortTradingEnabled
        )
    )

}
