package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.strategy.domain.model.StrategySettings
import ru.bolotov.tradebot.feature.strategy.presentation.R
import ru.bolotov.feature.strategy.presentation.core.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StrategyScreen(
    viewModel: StrategyViewModel
) {
    val state by viewModel.collectAsState()

    // Dialog states
    var showSimpleDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showVotingDialog by remember { mutableStateOf(false) }
    var showCandlestickDialog by remember { mutableStateOf(false) }
    var selectedStrategy by remember { mutableStateOf<StrategyItem?>(null) }
    var statusDialog by remember { mutableStateOf<StrategyStatusDialogData?>(null) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.ShowToast -> {
                statusDialog = StrategyStatusDialogData.Success
            }
            is Effect.ShowError -> {
                statusDialog = StrategyStatusDialogData.Error(effect.error)
            }
            is Effect.StrategyChanged -> {
                // Стратегия изменена, можно закрыть диалоги
                showSimpleDialog = false
                showConfirmationDialog = false
                showVotingDialog = false
                showCandlestickDialog = false
                selectedStrategy = null
            }
            is Effect.ShowSimpleStrategyDialog -> {
                selectedStrategy = state.availableStrategies.find { it.name == effect.name }
                showSimpleDialog = true
            }
            is Effect.ShowConfirmationDialog -> {
                showConfirmationDialog = true
            }
            is Effect.ShowVotingDialog -> {
                showVotingDialog = true
            }
            is Effect.ShowCandlestickDialog -> {
                showCandlestickDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Стратегии") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.handleAction(Action.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.handleAction(Action.LoadData) }
                            ) {
                                Text("Повторить")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Current strategy header
                        item {
                            CurrentStrategyHeader(
                                currentStrategy = state.currentStrategy,
                                settings = state.currentStrategySettings
                            )
                        }

                        // Strategy list
                        items(state.availableStrategies) { strategy ->
                            StrategyCard(
                                strategy = strategy,
                                onSelect = {
                                    viewModel.handleAction(
                                        Action.SelectStrategy(strategy.name, strategy.type)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Strategy config dialogs
    if (showSimpleDialog && selectedStrategy != null) {
        SimpleConfigDialog(
            strategyType = selectedStrategy!!.name,
            onConfirm = { strategyName ->
                viewModel.handleAction(Action.SwitchToSimple(strategyName))
            },
            onDismiss = {
                showSimpleDialog = false
                selectedStrategy = null
                viewModel.handleAction(Action.DismissDialog)
            }
        )
    }

    if (showConfirmationDialog) {
        ConfirmationConfigDialog(
            initialIndicators = state.confirmationIndicators,
            onConfirm = { indicators ->
                viewModel.handleAction(Action.SwitchToConfirmation(indicators))
            },
            onDismiss = {
                showConfirmationDialog = false
                viewModel.handleAction(Action.DismissDialog)
            }
        )
    }

    if (showVotingDialog) {
        VotingConfigDialog(
            initialWeights = state.votingWeights,
            onConfirm = { weights ->
                viewModel.handleAction(Action.SwitchToVoting(weights))
            },
            onDismiss = {
                showVotingDialog = false
                viewModel.handleAction(Action.DismissDialog)
            }
        )
    }

    if (showCandlestickDialog) {
        CandlestickConfigDialog(
            initialTimeframe = state.candlestickTimeframe,
            initialMinConfidence = state.candlestickMinConfidence,
            onConfirm = { timeframe, minConfidence ->
                viewModel.handleAction(Action.SwitchToCandlestick(timeframe, minConfidence))
            },
            onDismiss = {
                showCandlestickDialog = false
                viewModel.handleAction(Action.DismissDialog)
            }
        )
    }

    statusDialog?.let { status ->
        StrategyStatusDialog(
            status = status,
            onDismiss = { statusDialog = null }
        )
    }
}

@Composable
private fun CurrentStrategyHeader(
    currentStrategy: String,
    settings: StrategySettings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Текущая стратегия",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = currentStrategy,
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            StrategySettingsSummary(settings)
        }
    }
}

@Composable
private fun StrategySettingsSummary(settings: StrategySettings) {
    val description = when (settings) {
        StrategySettings.None -> stringResource(R.string.strategy_auto_selection_description)
        is StrategySettings.Candlestick -> stringResource(
            R.string.strategy_candlestick_settings,
            settings.timeframe,
            (settings.minConfidence * 100).toInt()
        )
        is StrategySettings.Confirmation -> stringResource(
            R.string.strategy_confirmation_settings,
            settings.indicators.joinToString()
        )
        is StrategySettings.Voting -> stringResource(
            R.string.strategy_voting_settings,
            settings.weights.entries.joinToString { "${it.key}: ${it.value}" }
        )
    }

    Text(
        text = description,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun StrategyStatusDialog(
    status: StrategyStatusDialogData,
    onDismiss: () -> Unit
) {
    val title = when (status) {
        StrategyStatusDialogData.Success -> stringResource(R.string.strategy_save_success_title)
        is StrategyStatusDialogData.Error -> stringResource(R.string.strategy_save_error_title)
    }
    val message = when (status) {
        StrategyStatusDialogData.Success -> stringResource(R.string.strategy_save_success_message)
        is StrategyStatusDialogData.Error -> status.message
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.strategy_dialog_confirm))
            }
        }
    )
}

private sealed interface StrategyStatusDialogData {
    data object Success : StrategyStatusDialogData
    data class Error(val message: String) : StrategyStatusDialogData
}
