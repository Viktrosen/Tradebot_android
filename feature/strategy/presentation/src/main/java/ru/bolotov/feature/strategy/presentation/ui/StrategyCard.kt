package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.strategy.presentation.core.StrategyItem
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.strategy.presentation.R

internal fun isNewStrategy(strategyName: String): Boolean =
    strategyName.trim().lowercase() in NEW_STRATEGY_IDS

private val NEW_STRATEGY_IDS = setOf("supertrend", "vwap")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StrategyCard(strategy: StrategyItem, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    val icon =
        when (strategy.name.trim().lowercase()) {
            "voting" -> UiR.drawable.ic_dashboard_outline
            "candlestick" -> UiR.drawable.ic_candles_outline
            "supertrend" -> UiR.drawable.ic_trend_outline
            "vwap" -> UiR.drawable.ic_positions_outline
            else -> UiR.drawable.ic_strategy_outline
        }
    TradebotCard(modifier.fillMaxWidth().clickable(onClick = onSelect)) {
        Row(
            Modifier.padding(TradebotDimens.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TradebotIcon(icon)
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                    verticalArrangement = Arrangement.spacedBy(TradebotDimens.smallGap),
                ) {
                    Text(strategy.name.uppercase(), style = MaterialTheme.typography.titleMedium)
                    if (isNewStrategy(strategy.name) && !strategy.isCurrent) {
                        TradebotBadge(
                            stringResource(R.string.strategies_new),
                            color = TradebotTheme.colors.brand,
                        )
                    }
                    if (strategy.isCurrent)
                        TradebotBadge(stringResource(R.string.strategies_current))
                }

                Text(
                    strategy.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
                if (strategy.name.equals("voting", ignoreCase = true)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EMA", "RSI", "MACD", "BB").forEach {
                            TradebotBadge(it, color = TradebotTheme.colors.textSecondary)
                        }
                    }
                }
            }
            IconButton(onClick = onSelect) {
                TradebotIcon(
                    UiR.drawable.ic_settings_outline,
                    contentDescription =
                        stringResource(R.string.strategies_configure, strategy.name),
                )
            }
        }
    }
}
