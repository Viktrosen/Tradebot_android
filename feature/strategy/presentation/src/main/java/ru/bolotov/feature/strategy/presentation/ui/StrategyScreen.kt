package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.feature.strategy.presentation.core.*
import ru.bolotov.tradebot.feature.strategy.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StrategyScreen(viewModel: StrategyViewModel) {
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

    StrategyContent(state, onAction = viewModel::handleAction)

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
            },
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
            },
        )
    }

    if (showVotingDialog) {
        VotingConfigDialog(
            initialWeights = state.votingWeights,
            onConfirm = { weights -> viewModel.handleAction(Action.SwitchToVoting(weights)) },
            onDismiss = {
                showVotingDialog = false
                viewModel.handleAction(Action.DismissDialog)
            },
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
            },
        )
    }

    statusDialog?.let { status ->
        StrategyStatusDialog(status = status, onDismiss = { statusDialog = null })
    }
}

@Composable
private fun StrategyStatusDialog(status: StrategyStatusDialogData, onDismiss: () -> Unit) {
    val title =
        when (status) {
            StrategyStatusDialogData.Success -> stringResource(R.string.strategy_save_success_title)
            is StrategyStatusDialogData.Error -> stringResource(R.string.strategy_save_error_title)
        }
    val message =
        when (status) {
            StrategyStatusDialogData.Success ->
                stringResource(R.string.strategy_save_success_message)
            is StrategyStatusDialogData.Error -> status.message
        }

    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        destructive = status is StrategyStatusDialogData.Error,
        text = { Text(message) },
        confirmButton = {
            TradebotDialogButton(onClick = onDismiss) {
                Text(stringResource(R.string.strategy_dialog_confirm))
            }
        },
    )
}

private sealed interface StrategyStatusDialogData {
    data object Success : StrategyStatusDialogData

    data class Error(val message: String) : StrategyStatusDialogData
}

@Preview
@Composable
private fun StrategyStatusDialogPreview() {
    TradebotTheme {
        StrategyStatusDialog(status = StrategyStatusDialogData.Success, onDismiss = {})
    }
}
