package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotCard
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.core.uikit.format.formatPercent
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun PositionResultCard(details: PositionDetailsUiModel) {
    TradebotCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(all = TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            val color =
                when {
                    details.pnl == null || details.pnl == 0.0 -> TradebotTheme.colors.text
                    details.pnl > 0 -> TradebotTheme.colors.profit
                    else -> TradebotTheme.colors.loss
                }
            Text(
                text =
                    details.pnl?.let { formatMoney(value = it, signed = true) }
                        ?: stringResource(R.string.details_no_data),
                style = TradebotTheme.typography.amount,
                color = color,
            )
            details.pnlPercent?.let {
                Text(
                    text = formatPercent(value = it, signed = true),
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            HorizontalDivider(color = TradebotTheme.colors.border)
            PositionDetailRow(
                label = stringResource(R.string.details_entry_price),
                value = detailsMoney(details.entryPrice),
            )
            if (details.isClosed) {
                PositionDetailRow(
                    label = stringResource(R.string.details_exit_price),
                    value = detailsMoney(details.closePrice),
                )
            }
            val units = details.quantity.toBigInteger() * details.lotSize.toBigInteger()
            PositionDetailRow(
                label = stringResource(R.string.details_quantity),
                value = stringResource(R.string.details_units, units.toString()),
            )
        }
    }
}

@Composable
internal fun PositionDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(weight = 1f),
            style = MaterialTheme.typography.bodyMedium,
            color = TradebotTheme.colors.textSecondary,
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}
