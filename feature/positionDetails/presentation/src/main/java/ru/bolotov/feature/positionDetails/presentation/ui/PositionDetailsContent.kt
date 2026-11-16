package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotCard
import ru.bolotov.core.uikit.components.TradebotErrorState
import ru.bolotov.core.uikit.components.TradebotHeader
import ru.bolotov.core.uikit.components.TradebotPullToRefreshBox
import ru.bolotov.core.uikit.components.TradebotSectionTitle
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.positionDetails.presentation.core.Action
import ru.bolotov.feature.positionDetails.presentation.core.CloseUiState
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel
import ru.bolotov.feature.positionDetails.presentation.core.UiState
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PositionDetailsContent(
    state: UiState,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val details = state.details
    Scaffold(
        containerColor = TradebotTheme.colors.background,
        topBar = {
            TradebotHeader(
                title = stringResource(R.string.details_title),
                subtitle = "",
                onBack = { onAction(Action.BackClicked) },
            )
        },
        bottomBar = {
            if (details != null && !details.isClosed) {
                PositionDetailsBottomBar(
                    closeState = state.closeState,
                    onClose = { onAction(Action.RequestClose) },
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        TradebotPullToRefreshBox(
            isRefreshing = state.isLoading || state.isRefreshing,
            onRefresh = { onAction(Action.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues = padding),
        ) {
            if (state.error && details == null) {
                TradebotErrorState(
                    message = stringResource(R.string.details_load_failed),
                    onRetry = { onAction(Action.LoadData) },
                )
            } else if (details != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = TradebotDimens.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(TradebotDimens.sectionGap),
                ) {
                    item { PositionPriceHeader(details = details, closeState = state.closeState) }
                    item {
                        TradebotSectionTitle(
                            title =
                                stringResource(
                                    if (details.isClosed) R.string.details_trade_result
                                    else R.string.details_result
                                )
                        )
                    }
                    item { PositionResultCard(details = details) }
                    item { PositionOpeningCard(details = details) }
                    if (!details.isClosed) {
                        item {
                            TradebotSectionTitle(
                                title = stringResource(R.string.details_protection)
                            )
                        }
                        item { PositionProtectionCard(details = details) }
                    } else {
                        item {
                            TradebotSectionTitle(
                                title = stringResource(R.string.details_close_reason)
                            )
                        }
                        item {
                            TradebotCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = closeReasonLabel(details.closeReason),
                                    modifier = Modifier.padding(all = TradebotDimens.cardPadding),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                    val explanation =
                        if (details.isClosed) details.closeExplanation else details.aiExplanation
                    if (!explanation.isNullOrBlank()) {
                        item {
                            PositionExplanationCard(
                                title =
                                    stringResource(
                                        if (details.isClosed) R.string.details_why_closed
                                        else R.string.details_why_open
                                    ),
                                subtitle =
                                    stringResource(
                                        if (details.isClosed) R.string.details_close_explanation
                                        else R.string.details_ai_explanation
                                    ),
                                explanation = explanation,
                                expanded = state.isExplanationExpanded,
                                onToggle = { onAction(Action.ToggleExplanation) },
                            )
                        }
                    }
                    if (details.isClosed && !details.aiExplanation.isNullOrBlank()) {
                        item {
                            PositionExplanationCard(
                                title = stringResource(R.string.details_why_open),
                                subtitle = stringResource(R.string.details_ai_explanation),
                                explanation = details.aiExplanation,
                                expanded = state.isEntryExplanationExpanded,
                                onToggle = { onAction(Action.ToggleEntryExplanation) },
                            )
                        }
                    }
                }
            }
        }
    }
    if (state.closeState == CloseUiState.Confirming && details != null && !details.isClosed) {
        ClosePositionConfirmationDialog(
            details = details,
            onConfirm = { onAction(Action.ConfirmClose) },
            onDismiss = { onAction(Action.DismissCloseDialog) },
        )
    }
}

@Composable
private fun closeReasonLabel(reason: String?): String =
    stringResource(
        when (reason) {
            "MANUAL",
            "MANUAL_CLOSE" -> R.string.details_reason_manual
            "STOP_LOSS" -> R.string.details_reason_stop
            "TAKE_PROFIT" -> R.string.details_reason_take_profit
            "PROFIT_PROTECTION" -> R.string.details_reason_protection
            "EMERGENCY",
            "EMERGENCY_CLOSE" -> R.string.details_reason_emergency
            "SIGNAL_CLOSE" -> R.string.details_reason_signal
            "BROKER_POSITION_RECONCILIATION" -> R.string.details_reason_reconciliation
            "PROTECTION_SETUP_FAILED" -> R.string.details_reason_protection_failed
            else -> R.string.details_reason_other
        }
    )

private fun previewDetails(closed: Boolean = false) =
    PositionDetailsUiModel(
        positionId = "preview",
        instrumentId = "preview",
        instrumentName = "Сбербанк",
        positionSide = "LONG",
        quantity = 1,
        lotSize = 10,
        entryPrice = 260.0,
        currentPrice = if (closed) null else 264.8,
        pnl = 48.0,
        pnlPercent = 1.85,
        isClosed = closed,
        entryTime = "2026-11-16T07:32:00Z",
        entryStrategyName = "Cross EMA",
        aiExplanation =
            "EMA5 пересекла EMA21 снизу вверх. На момент входа ИИ подтвердил сигнал BUY.",
        brokerStopLossPrice = if (closed) null else 254.8,
        managedExitPrice = if (closed) null else 262.5,
        takeProfitPrice = if (closed) null else 267.8,
        closeTime = if (closed) "2026-11-16T11:15:00Z" else null,
        closePrice = if (closed) 264.8 else null,
        closeExplanation = if (closed) "Позиция закрыта по запросу пользователя." else null,
        closeReason = if (closed) "MANUAL_CLOSE" else null,
    )

@Preview(name = "Open", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsContentPreview() {
    TradebotTheme {
        PositionDetailsContent(
            state = UiState(details = previewDetails(), isLoading = false),
            onAction = {},
        )
    }
}

@Preview(name = "Expanded", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsExpandedPreview() {
    TradebotTheme {
        PositionDetailsContent(
            state =
                UiState(
                    details = previewDetails(),
                    isLoading = false,
                    isExplanationExpanded = true,
                ),
            onAction = {},
        )
    }
}

@Preview(name = "Confirm", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsConfirmPreview() {
    TradebotTheme {
        PositionDetailsContent(
            state =
                UiState(
                    details = previewDetails(),
                    isLoading = false,
                    closeState = CloseUiState.Confirming,
                ),
            onAction = {},
        )
    }
}

@Preview(name = "Closing", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsClosingPreview() {
    TradebotTheme {
        PositionDetailsContent(
            state =
                UiState(
                    details = previewDetails(),
                    isLoading = false,
                    closeState = CloseUiState.AwaitingConfirmation,
                ),
            onAction = {},
        )
    }
}

@Preview(name = "Closed", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsClosedPreview() {
    TradebotTheme {
        PositionDetailsContent(
            state =
                UiState(
                    details = previewDetails(closed = true),
                    isLoading = false,
                    isExplanationExpanded = true,
                ),
            onAction = {},
        )
    }
}

@Preview(name = "Light", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun PositionDetailsLightPreview() {
    TradebotTheme(darkTheme = false) {
        PositionDetailsContent(
            state = UiState(details = previewDetails(), isLoading = false),
            onAction = {},
        )
    }
}
