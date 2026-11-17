package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.components.TradebotPullToRefreshBox
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.strategy.presentation.core.*
import ru.bolotov.feature.strategy.presentation.core.StrategyType
import ru.bolotov.feature.strategy.presentation.core.UiState
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.strategy.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StrategyContent(state: UiState, onAction: (Action) -> Unit) {
    Scaffold(
        topBar = {
            TradebotHeader(
                title = stringResource(R.string.strategies_title),
                subtitle = stringResource(R.string.strategies_subtitle),
            )
        }
    ) { paddingValues ->
        TradebotPullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(Action.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage.ifBlank {
                                    stringResource(R.string.strategy_load_error)
                                },
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { onAction(Action.LoadData) }) {
                                Text(stringResource(UiR.string.tb_retry))
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(TradebotDimens.screenPadding),
                        verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
                    ) {
                        // Current strategy header
                        item {
                            AutomaticStrategyHeader()
                        }

                        item {
                            TradebotSectionTitle(
                                title = stringResource(R.string.strategies_parameters),
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }

                        // Strategy list
                        items(state.availableStrategies) { strategy ->
                            StrategyCard(
                                strategy = strategy,
                                onConfigure = {
                                    onAction(Action.OpenConfigDialog(type = strategy.type))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomaticStrategyHeader() {
    TradebotCard(
        modifier = Modifier.fillMaxWidth(),
        shape = TradebotTheme.shapes.clipped,
        borderColor = TradebotTheme.colors.accent.copy(alpha = 0.6f),
    ) {
        Row(
            modifier = Modifier.padding(TradebotDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            TradebotIcon(
                resource = UiR.drawable.ic_strategy_outline,
                tint = TradebotTheme.colors.accent,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                TradebotBadge(stringResource(R.string.strategies_auto))
                Text(
                    text = stringResource(R.string.strategy_automatic_selection_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.strategy_auto_selection_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Preview(name = "Strategies · automatic", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun StrategyContentPreview() {
    TradebotTheme {
        StrategyContent(
            state = UiState(
                availableStrategies =
                    listOf(
                        StrategyItem(
                            name = "Voting",
                            type = StrategyType.VOTING,
                            description = "Взвешенное голосование EMA, RSI, MACD, BB",
                        ),
                        StrategyItem(
                            name = "Candlestick",
                            type = StrategyType.CANDLESTICK,
                            description = "Свечные паттерны: Engulfing, Hammer, Doji",
                        ),
                        StrategyItem(
                            name = "SuperTrend",
                            type = StrategyType.SIMPLE,
                            description = "Следование за трендом по ATR и динамическим полосам",
                        ),
                        StrategyItem(
                            name = "VWAP",
                            type = StrategyType.SIMPLE,
                            description = "Цена относительно средневзвешенной по объёму",
                        ),
                    ),
            ),
            onAction = {},
        )
    }
}
