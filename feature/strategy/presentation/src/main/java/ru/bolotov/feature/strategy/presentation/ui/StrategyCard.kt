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
fun StrategyCard(strategy: StrategyItem, onConfigure: () -> Unit, modifier: Modifier = Modifier) {
    val isConfigurable = strategy.type.configurationDialog != null
    val icon =
        when (strategy.name.trim().lowercase()) {
            "voting" -> UiR.drawable.ic_dashboard_outline
            "candlestick" -> UiR.drawable.ic_candles_outline
            "supertrend" -> UiR.drawable.ic_trend_outline
            "vwap" -> UiR.drawable.ic_positions_outline
            else -> UiR.drawable.ic_strategy_outline
        }
    TradebotCard(
        modifier = modifier.fillMaxWidth().then(
            if (isConfigurable) Modifier.clickable(onClick = onConfigure) else Modifier
        ),
    ) {
        Row(
            modifier = Modifier.padding(TradebotDimens.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TradebotIcon(icon)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                    verticalArrangement = Arrangement.spacedBy(TradebotDimens.smallGap),
                ) {
                    Text(
                        text = strategy.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (isNewStrategy(strategy.name)) {
                        TradebotBadge(
                            text = stringResource(R.string.strategies_new),
                            color = TradebotTheme.colors.brand,
                        )
                    }
                }

                Text(
                    text = strategy.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
            if (isConfigurable) {
                IconButton(onClick = onConfigure) {
                    TradebotIcon(
                        resource = UiR.drawable.ic_settings_outline,
                        contentDescription = stringResource(R.string.strategies_configure, strategy.name),
                    )
                }
            }
        }
    }
}
