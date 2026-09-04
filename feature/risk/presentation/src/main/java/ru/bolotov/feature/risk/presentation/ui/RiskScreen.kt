package ru.bolotov.feature.risk.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.tradebot.feature.risk.presentation.R
import ru.bolotov.feature.risk.presentation.core.Action
import ru.bolotov.feature.risk.presentation.core.Effect
import ru.bolotov.feature.risk.presentation.core.RiskConfigUi
import ru.bolotov.feature.risk.presentation.core.RiskViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RiskScreen(
    viewModel: RiskViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var statusDialog by remember { mutableStateOf<RiskStatusDialogData?>(null) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.ShowResetConfirmation -> showResetDialog = true
            is Effect.DismissResetConfirmation -> showResetDialog = false
            is Effect.NavigateBack -> onNavigateBack()
            is Effect.ShowToast -> statusDialog = RiskStatusDialogData.Success(effect.message)
            is Effect.ShowError -> statusDialog = RiskStatusDialogData.Error(effect.error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.risk_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.risk_screen_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            RiskBottomBar(
                onSave = { viewModel.handleAction(Action.Save) },
                onReset = { viewModel.handleAction(Action.ShowResetConfirmation) },
                isSaving = state.isSaving,
                isResetting = state.isResetting
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
            if (state.errorMessage != null) {
                ErrorContent(
                    message = state.errorMessage.orEmpty(),
                    onRetry = { viewModel.handleAction(Action.LoadData) }
                )
            } else {
                RiskContent(
                    config = state.riskConfig,
                    onUpdatePositionSize = {
                        viewModel.handleAction(Action.UpdatePositionSizePercent(it))
                    },
                    onUpdateStopLoss = {
                        viewModel.handleAction(Action.UpdateStopLossPercent(it))
                    },
                    onUpdateTakeProfit = {
                        viewModel.handleAction(Action.UpdateTakeProfitPercent(it))
                    },
                    onUpdateCapitalUsage = {
                        viewModel.handleAction(Action.UpdateMaxCapitalUsage(it))
                    },
                    onUpdateMaxPositions = {
                        viewModel.handleAction(Action.UpdateMaxPositions(it))
                    },
                    onUpdateShortTradingEnabled = {
                        viewModel.handleAction(Action.RequestShortTradingEnabled(it))
                    }
                )
            }
        }
    }

    if (showResetDialog) {
        ResetConfirmationDialog(
            onConfirm = { viewModel.handleAction(Action.ConfirmReset) },
            onDismiss = { viewModel.handleAction(Action.DismissResetConfirmation) }
        )
    }

    if (state.isShortTradingConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.handleAction(Action.DismissShortTradingConfirmation) },
            title = { Text(stringResource(R.string.risk_short_confirmation_title)) },
            text = { Text(stringResource(R.string.risk_short_confirmation_message)) },
            confirmButton = {
                Button(onClick = { viewModel.handleAction(Action.ConfirmShortTradingEnabled) }) {
                    Text(stringResource(R.string.risk_short_confirmation_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    viewModel.handleAction(Action.DismissShortTradingConfirmation)
                }) {
                    Text(stringResource(R.string.risk_dialog_cancel))
                }
            }
        )
    }

    statusDialog?.let { dialog ->
        RiskStatusDialog(
            status = dialog,
            onDismiss = { statusDialog = null }
        )
    }
}

@Composable
private fun RiskStatusDialog(
    status: RiskStatusDialogData,
    onDismiss: () -> Unit
) {
    val title = when (status) {
        is RiskStatusDialogData.Success -> stringResource(R.string.risk_save_success_title)
        is RiskStatusDialogData.Error -> stringResource(R.string.risk_save_error_title)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(status.message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.risk_dialog_ok))
            }
        }
    )
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.risk_screen_retry))
            }
        }
    }
}

@Composable
private fun RiskContent(
    config: RiskConfigUi?,
    onUpdatePositionSize: (Double) -> Unit,
    onUpdateStopLoss: (Double) -> Unit,
    onUpdateTakeProfit: (Double) -> Unit,
    onUpdateCapitalUsage: (Double) -> Unit,
    onUpdateMaxPositions: (Int) -> Unit,
    onUpdateShortTradingEnabled: (Boolean) -> Unit
) {
    if (config == null) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PercentageRiskCard(
                title = stringResource(R.string.risk_stop_loss_title),
                description = stringResource(R.string.risk_stop_loss_description),
                value = config.stopLossPercent,
                percentRange = 1..20,
                onValueChange = onUpdateStopLoss
            )
        }
        item {
            PercentageRiskCard(
                title = stringResource(R.string.risk_take_profit_title),
                description = stringResource(R.string.risk_take_profit_description),
                value = config.takeProfitPercent,
                percentRange = 1..50,
                onValueChange = onUpdateTakeProfit
            )
        }
        item {
            PercentageRiskCard(
                title = stringResource(R.string.risk_position_size_title),
                description = stringResource(R.string.risk_position_size_description),
                value = config.positionSizePercent,
                percentRange = 1..80,
                onValueChange = onUpdatePositionSize
            )
        }
        item {
            PercentageRiskCard(
                title = stringResource(R.string.risk_capital_usage_title),
                description = stringResource(R.string.risk_capital_usage_description),
                value = config.maxCapitalUsage,
                percentRange = 10..95,
                onValueChange = onUpdateCapitalUsage
            )
        }
        item {
            RiskCard(
                title = stringResource(R.string.risk_max_positions_title),
                description = stringResource(R.string.risk_max_positions_description),
                value = config.maxPositions.toString(),
                unit = ""
            ) {
                RiskSlider(
                    value = config.maxPositions.toFloat(),
                    onValueChange = { onUpdateMaxPositions(it.toInt()) },
                    valueRange = 1f..50f,
                    steps = 48,
                    formatValue = {
                        stringResource(R.string.risk_positions_value, it.toInt())
                    }
                )
            }
        }
        item {
            RiskCard(
                title = stringResource(R.string.risk_short_trading_title),
                description = stringResource(R.string.risk_short_trading_description),
                value = if (config.shortTradingEnabled) {
                    stringResource(R.string.risk_short_trading_enabled)
                } else {
                    stringResource(R.string.risk_short_trading_disabled)
                },
                unit = ""
            ) {
                Switch(
                    checked = config.shortTradingEnabled,
                    onCheckedChange = onUpdateShortTradingEnabled
                )
            }
        }
    }
}

@Composable
private fun PercentageRiskCard(
    title: String,
    description: String,
    value: Double,
    percentRange: IntRange,
    onValueChange: (Double) -> Unit
) {
    RiskCard(
        title = title,
        description = description,
        value = value.toPercentText(),
        unit = ""
    ) {
        RiskSlider(
            value = value.toPercentValue(),
            onValueChange = { onValueChange(it.toInt() / 100.0) },
            valueRange = percentRange.first.toFloat()..percentRange.last.toFloat(),
            steps = percentRange.last - percentRange.first - 1,
            formatValue = { "${it.toInt()}%" }
        )
    }
}

@Composable
private fun RiskBottomBar(
    onSave: () -> Unit,
    onReset: () -> Unit,
    isSaving: Boolean,
    isResetting: Boolean
) {
    Surface(tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                enabled = !isSaving && !isResetting
            ) {
                if (isResetting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.risk_screen_reset))
                }
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = !isSaving && !isResetting
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.risk_screen_save))
                }
            }
        }
    }
}

private fun Double.toPercentText(): String = "${(this * 100).roundToInt()}%"

private fun Double.toPercentValue(): Float = (this * 100).roundToInt().toFloat()

private sealed interface RiskStatusDialogData {
    val message: String

    data class Success(override val message: String) : RiskStatusDialogData
    data class Error(override val message: String) : RiskStatusDialogData
}
