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
import ru.bolotov.feature.strategy.domain.model.StrategySettings
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
                stringResource(R.string.strategies_title),
                stringResource(R.string.strategies_subtitle),
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
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(TradebotDimens.screenPadding),
                        verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
                    ) {
                        // Current strategy header
                        item {
                            CurrentStrategyHeader(
                                currentStrategy = state.currentStrategy,
                                settings = state.currentStrategySettings,
                            )
                        }

                        item {
                            TradebotSectionTitle(
                                stringResource(R.string.strategies_parameters),
                                Modifier.padding(top = 8.dp),
                            )
                        }

                        // Strategy list
                        items(state.availableStrategies) { strategy ->
                            StrategyCard(
                                strategy = strategy,
                                onSelect = {
                                    onAction(Action.SelectStrategy(strategy.name, strategy.type))
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
private fun CurrentStrategyHeader(currentStrategy: String, settings: StrategySettings) {
    TradebotCard(
        Modifier.fillMaxWidth(),
        shape = TradebotTheme.shapes.clipped,
        borderColor = TradebotTheme.colors.accent.copy(alpha = 0.6f),
    ) {
        Row(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            TradebotIcon(UiR.drawable.ic_strategy_outline, tint = TradebotTheme.colors.accent)
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                if (settings == StrategySettings.None)
                    TradebotBadge(stringResource(R.string.strategies_auto))
                Text(currentStrategy, style = MaterialTheme.typography.titleMedium)
                StrategySettingsSummary(settings)
            }
        }
    }
}

@Composable
private fun StrategySettingsSummary(settings: StrategySettings) {
    val description =
        when (settings) {
            StrategySettings.None -> stringResource(R.string.strategy_auto_selection_description)
            is StrategySettings.Candlestick ->
                stringResource(
                    R.string.strategy_candlestick_settings,
                    settings.timeframe,
                    (settings.minConfidence * 100).toInt(),
                )
            is StrategySettings.Confirmation ->
                stringResource(
                    R.string.strategy_confirmation_settings,
                    settings.indicators.joinToString(),
                )
            is StrategySettings.Voting ->
                stringResource(
                    R.string.strategy_voting_settings,
                    settings.weights.entries.joinToString { "${it.key}: ${it.value}" },
                )
        }

    Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}

@Preview(name = "Strategies · automatic", showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun StrategyContentPreview() {
    TradebotTheme {
        StrategyContent(
            UiState(
                currentStrategy = "Автоматический выбор",
                availableStrategies =
                    listOf(
                        StrategyItem(
                            "Voting",
                            StrategyType.VOTING,
                            "Взвешенное голосование EMA, RSI, MACD, BB",
                        ),
                        StrategyItem(
                            "Candlestick",
                            StrategyType.CANDLESTICK,
                            "Свечные паттерны: Engulfing, Hammer, Doji",
                        ),
                        StrategyItem(
                            "SuperTrend",
                            StrategyType.SIMPLE,
                            "Следование за трендом по ATR и динамическим полосам",
                        ),
                        StrategyItem(
                            "VWAP",
                            StrategyType.SIMPLE,
                            "Цена относительно средневзвешенной по объёму",
                        ),
                    ),
            ),
            onAction = {},
        )
    }
}
