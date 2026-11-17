package ru.bolotov.feature.strategy.presentation.ui

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

    var statusDialog by remember { mutableStateOf<StrategyStatusDialogData?>(null) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            Effect.SettingsSaved -> {
                statusDialog = StrategyStatusDialogData.Success
            }
            is Effect.ShowError -> {
                statusDialog = StrategyStatusDialogData.Error(effect.error)
            }
        }
    }

    StrategyContent(state = state, onAction = viewModel::handleAction)

    // Strategy config dialogs
    if (state.dialog == StrategyDialog.Confirmation) {
        ConfirmationConfigDialog(
            initialIndicators = state.confirmationIndicators,
            onConfirm = { indicators ->
                if (!state.isSaving) viewModel.handleAction(Action.SaveConfirmation(indicators = indicators))
            },
            onDismiss = {
                viewModel.handleAction(Action.DismissDialog)
            },
        )
    }

    if (state.dialog == StrategyDialog.Voting) {
        VotingConfigDialog(
            draft = state.votingDraft,
            isSaving = state.isSaving,
            onParticipantChange = { participant, enabled ->
                viewModel.handleAction(Action.SetVotingParticipant(participant = participant, enabled = enabled))
            },
            onWeightChange = { participant, weight ->
                viewModel.handleAction(Action.SetVotingWeight(participant = participant, weight = weight))
            },
            onConfirm = { weights ->
                if (!state.isSaving) viewModel.handleAction(Action.SaveVoting(weights = weights))
            },
            onDismiss = {
                viewModel.handleAction(Action.DismissDialog)
            },
        )
    }

    if (state.dialog == StrategyDialog.Candlestick) {
        CandlestickConfigDialog(
            initialTimeframe = state.candlestickTimeframe,
            initialMinConfidence = state.candlestickMinConfidence,
            onConfirm = { timeframe, minConfidence ->
                if (!state.isSaving) viewModel.handleAction(
                    Action.SaveCandlestick(timeframe = timeframe, minConfidence = minConfidence)
                )
            },
            onDismiss = {
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
            is StrategyStatusDialogData.Error -> status.message.ifBlank {
                stringResource(R.string.strategy_save_error_title)
            }
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
