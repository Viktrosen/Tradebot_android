package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.*
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.core.uikit.format.formatPercent
import ru.bolotov.feature.positions.presentation.core.UiPosition
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.positions.presentation.R

/** Text-first card: no remote logos, invented ticker or inferred protection values. */
@Composable
fun PositionCard(
    position: UiPosition,
    onCloseClick: () -> Unit,
    onInstrumentClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TradebotTheme.colors
    val pnlColor =
        when {
            position.isProfit -> colors.profit
            position.isLoss -> colors.loss
            else -> colors.textSecondary
        }
    TradebotCard(modifier.fillMaxWidth().clickable(onClick = onInstrumentClick)) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(position.instrumentName, style = MaterialTheme.typography.titleMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TradebotBadge(
                            stringResource(
                                if (position.positionSide == "SHORT") UiR.string.tb_short
                                else UiR.string.tb_long
                            ),
                            color =
                                if (position.positionSide == "SHORT") colors.short
                                else colors.accent,
                        )
                        if (position.isClosed)
                            TradebotBadge(
                                stringResource(R.string.position_closed),
                                color = colors.textSecondary,
                            )
                    }
                    val details =
                        listOfNotNull(
                                position.entryStrategyName?.takeIf {
                                    !position.isClosed && it.isNotBlank()
                                },
                                pluralStringResource(
                                    UiR.plurals.tb_lot_quantity,
                                    position.quantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                                    position.quantity
                                ),
                            )
                            .joinToString(" · ")
                    Text(
                        details,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                if (!position.infoExplanation.isNullOrBlank()) {
                    IconButton(onClick = onInfoClick) {
                        TradebotIcon(
                            UiR.drawable.ic_info_outline,
                            contentDescription =
                                stringResource(
                                    if (position.isClosed) R.string.position_close_reason_title
                                    else R.string.position_ai_explanation_title
                                ),
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap)
            ) {
                PositionPrice(
                    stringResource(R.string.positions_entry),
                    position.entryPrice,
                    Modifier.weight(1f)
                )
                if (!position.isClosed)
                    position.currentPrice?.let {
                        PositionPrice(
                            stringResource(R.string.positions_current),
                            it,
                            Modifier.weight(1f)
                        )
                    }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        formatMoney(position.pnl, signed = true),
                        color = pnlColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    position.pnlPercent?.let {
                        Text(
                            formatPercent(it, signed = true),
                            color = pnlColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            if (
                !position.isClosed &&
                    (position.brokerStopLossPrice != null || position.managedExitPrice != null)
            ) {
                TradebotCard(Modifier.fillMaxWidth(), containerColor = colors.surfaceRaised) {
                    Row(
                        Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TradebotIcon(UiR.drawable.ic_risk_outline)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(TradebotDimens.smallGap)
                        ) {
                            position.brokerStopLossPrice?.let {
                                Text(
                                    stringResource(R.string.positions_broker_sl, it.money()),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            position.managedExitPrice?.let {
                                Text(
                                    stringResource(
                                        R.string.positions_protection,
                                        position.profitProtectionStage.toProfitProtectionLabel(),
                                        it.money()
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.accent
                                )
                            }
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(TradebotDimens.smallGap),
                ) {
                    position.entryTime?.let {
                        Text(
                            stringResource(R.string.positions_opened, it.toPositionDateTime()),
                            style = TradebotTheme.typography.label,
                            color = colors.textSecondary
                        )
                    }
                    if (position.isClosed)
                        position.closeTime?.let {
                            Text(
                                stringResource(
                                    R.string.positions_closed_at,
                                    it.toPositionDateTime(),
                                ),
                                style = TradebotTheme.typography.label,
                                color = colors.textSecondary
                            )
                        }
                }
                if (!position.isClosed)
                    TradebotButton(
                        stringResource(R.string.position_close),
                        onCloseClick,
                        outlined = true,
                        destructive = true
                    )
            }
        }
    }
}

@Composable
private fun PositionPrice(
    label: String,
    value: Double?,
    modifier: Modifier
) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = TradebotTheme.colors.textSecondary
        )
        Text(value.money(), style = MaterialTheme.typography.titleSmall)
    }
}

@Composable private fun Double?.money() = formatMoney(this)

@Composable
private fun String.toPositionDateTime(): String {
    val locale = LocalConfiguration.current.locales[0]
    return runCatching {
            Instant.parse(this)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale))
        }
        .getOrElse { this }
}

@Composable
private fun String?.toProfitProtectionLabel(): String =
    when (this) {
        "BREAKEVEN" -> stringResource(R.string.positions_breakeven)
        "TRAILING" -> stringResource(R.string.positions_trailing)
        else -> stringResource(R.string.positions_active)
    }
