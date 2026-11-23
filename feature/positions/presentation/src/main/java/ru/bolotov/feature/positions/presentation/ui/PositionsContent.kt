package ru.bolotov.feature.positions.presentation.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotCard
import ru.bolotov.core.uikit.components.TradebotHeader
import ru.bolotov.core.uikit.components.TradebotPullToRefreshBox
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.feature.positions.presentation.core.Action
import ru.bolotov.feature.positions.presentation.core.PositionFilter
import ru.bolotov.feature.positions.presentation.core.UiPosition
import ru.bolotov.feature.positions.presentation.core.UiState
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.positions.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PositionsContent(
    state: UiState,
    onAction: (Action) -> Unit,
    onNavigateToInstrument: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TradebotHeader(
                stringResource(R.string.positions_title),
                stringResource(R.string.positions_subtitle),
            )
        }
    ) { paddingValues ->
        TradebotPullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(Action.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Stats row
                StatsRow(
                    totalPnl = state.totalPnl,
                    openCount = state.openPositionsCount,
                    closedCount = state.closedPositionsCount,
                    modifier = Modifier.padding(TradebotDimens.cardPadding)
                )

                // Filter bar
                PositionsFilterBar(
                    currentFilter = state.filterType,
                    onFilterSelected = { onAction(Action.Filter(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Positions list
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.errorMessage != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = state.errorMessage!!,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { onAction(Action.LoadData) }) {
                                        Text(stringResource(UiR.string.tb_retry))
                                    }
                                }
                            }
                        }

                        state.filteredPositions.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(stringResource(R.string.positions_empty))
                                    Text(
                                        text =
                                            when (state.filterType) {
                                                PositionFilter.ALL ->
                                                    stringResource(R.string.positions_empty_all)
                                                PositionFilter.OPEN ->
                                                    stringResource(R.string.positions_empty_open)
                                                PositionFilter.PROFIT ->
                                                    stringResource(R.string.positions_empty_profit)
                                                PositionFilter.LOSS ->
                                                    stringResource(R.string.positions_empty_loss)
                                            },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(TradebotDimens.screenPadding),
                                verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
                            ) {
                                items(state.filteredPositions) { position ->
                                    PositionCard(
                                        position = position,
                                        onCloseClick = {
                                            onAction(Action.ClosePosition(position.id))
                                        },
                                        onInstrumentClick = {
                                            onAction(
                                                Action.NavigateToInstrument(position.instrumentId)
                                            )
                                            onAction(Action.OpenPositionDetails(positionId = position.id))
                                        },
                                        onInfoClick = {
                                            onAction(Action.ShowPositionInfo(position.id))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(
    totalPnl: Double?,
    openCount: Int,
    closedCount: Int,
    modifier: Modifier = Modifier
) {

    TradebotCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(TradebotDimens.cardPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(
                title = stringResource(R.string.positions_total),
                value = formatMoney(totalPnl, signed = true),
                valueColor =
                    when {
                        totalPnl != null && totalPnl > 0 -> TradebotTheme.colors.profit
                        totalPnl != null && totalPnl < 0 -> TradebotTheme.colors.loss
                        else -> TradebotTheme.colors.text
                    }
            )

            StatItem(
                title = stringResource(R.string.positions_open_count),
                value = "$openCount",
                valueColor = MaterialTheme.colorScheme.primary
            )

            StatItem(
                title = stringResource(R.string.positions_closed_count),
                value = "$closedCount",
                valueColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = valueColor
        )
    }
}

@Preview(
    name = "Positions · long, short, closed",
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)
@Composable
private fun PositionsContentPreview() {
    val long =
        UiPosition(
            id = "preview-long",
            instrumentId = "preview-only",
            instrumentName = "Сбербанк",
            direction = "BUY",
            positionSide = "LONG",
            quantity = 1,
            entryPrice = 267.0,
            currentPrice = 268.2,
            pnl = 12.0,
            pnlPercent = 0.45,
            isClosed = false,
            entryTime = "2026-11-12T07:15:00Z",
            closeTime = null,
            entryStrategyName = "Cross EMA",
            aiExplanation = "Демонстрационные данные превью",
            closeExplanation = null,
            brokerStopLossPrice = 261.66,
            managedExitPrice = 267.10,
            profitProtectionStage = "BREAKEVEN"
        )
    val short =
        long.copy(
            id = "preview-short",
            instrumentName = "Сургутнефтегаз-п",
            direction = "SELL",
            positionSide = "SHORT",
            entryPrice = 41.9,
            currentPrice = 42.2,
            pnl = -3.0,
            pnlPercent = -0.72,
            brokerStopLossPrice = 42.74,
            managedExitPrice = null,
            profitProtectionStage = null,
            entryStrategyName = "SuperTrend"
        )
    val closed =
        short.copy(
            id = "preview-closed",
            instrumentName = "Русал",
            isClosed = true,
            pnl = 5.0,
            pnlPercent = 0.5,
            closeTime = "2026-11-12T09:00:00Z",
            aiExplanation = null,
            closeExplanation = "Защита прибыли"
        )
    TradebotTheme {
        PositionsContent(
            UiState(
                filteredPositions = persistentListOf(
                    long,
                    short,
                    closed
                ),
                totalPnl = 14.0,
                openPositionsCount = 2,
                closedPositionsCount = 1
            ),
            onAction = {},
            onNavigateToInstrument = {}
        )
    }
}
