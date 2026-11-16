package ru.bolotov.feature.dashboard.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.core.uikit.components.TradebotErrorState
import ru.bolotov.core.uikit.components.TradebotHeader
import ru.bolotov.core.uikit.components.TradebotPullToRefreshBox
import ru.bolotov.feature.dashboard.presentation.core.Action
import ru.bolotov.feature.dashboard.presentation.core.DashboardViewModel
import ru.bolotov.feature.dashboard.presentation.core.Effect
import ru.bolotov.tradebot.feature.dashboard.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToRisk: () -> Unit,
    onNavigateToPositions: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onOpenPositionDetails: (String) -> Unit,
    onNavigateToInstruments: () -> Unit = {},
) {
    val state by viewModel.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.handleAction(Action.ScreenResumed) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.NavigateToPositionDetails -> onOpenPositionDetails(effect.positionId)
            is Effect.NavigateToRisk -> onNavigateToRisk()
            is Effect.NavigateToPositions -> onNavigateToPositions()
            is Effect.NavigateToStrategy -> onNavigateToStrategy()
            is Effect.ShowToast -> {
                /* TODO: Show toast */
            }

            is Effect.ShowError -> {
                /* TODO: Show error dialog */
            }
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                isRunning = state.botStatus?.running ?: false,
                allTradingUnavailable = state.botStatus?.allTradingUnavailable ?: false,
            )
        }
    ) { paddingValues ->
        TradebotPullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.handleAction(Action.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            when {
                state.errorMessage != null ->
                    TradebotErrorState(
                        message = state.errorMessage!!,
                        onRetry = {
                            viewModel.handleAction(Action.Refresh)
                        }
                    )

                else ->
                    DashboardContent(
                        state = state,
                        onStartBot = { viewModel.handleAction(Action.StartBot) },
                        onStopBot = { viewModel.handleAction(Action.StopBot) },
                        onNavigateToStrategy = {
                            viewModel.handleAction(Action.NavigateToStrategy)
                        },
                        onNavigateToRisk = { viewModel.handleAction(Action.NavigateToRisk) },
                        onNavigateToPositions = {
                            viewModel.handleAction(Action.NavigateToPositions)
                        },
                        onNavigateToInstruments = onNavigateToInstruments,
                        onShowAllPositions = { viewModel.handleAction(Action.NavigateToPositions) },
                        onOpenPositionDetails = { viewModel.handleAction(Action.OpenPositionDetails(positionId = it)) },
                    )
            }
        }
    }
}

@Composable
private fun DashboardTopBar(isRunning: Boolean, allTradingUnavailable: Boolean) {
    TradebotHeader(
        stringResource(R.string.dashboard_title),
        stringResource(R.string.dashboard_subtitle),
    )
}
