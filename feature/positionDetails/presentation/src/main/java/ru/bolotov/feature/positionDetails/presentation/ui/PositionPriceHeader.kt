package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotBadge
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.core.uikit.format.formatMoney
import ru.bolotov.core.uikit.format.formatPercent
import ru.bolotov.feature.positionDetails.presentation.core.CloseUiState
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun PositionPriceHeader(details: PositionDetailsUiModel, closeState: CloseUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap)) {
        Text(text = details.instrumentName, style = TradebotTheme.typography.screenTitle)
        Row(horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap)) {
            TradebotBadge(
                text = details.positionSide,
                color =
                    if (details.positionSide == "SHORT") TradebotTheme.colors.short
                    else TradebotTheme.colors.profit,
            )
            TradebotBadge(
                text =
                    stringResource(
                        when {
                            details.isClosed -> R.string.details_closed
                            closeState != CloseUiState.Idle &&
                                closeState != CloseUiState.Confirming -> R.string.details_closing
                            else -> R.string.details_open
                        }
                    ),
                color = TradebotTheme.colors.textSecondary,
            )
        }
        if (!details.isClosed) {
            Text(text = detailsMoney(details.currentPrice), style = TradebotTheme.typography.amount)
            details.currentPrice?.let { price ->
                if (details.entryPrice > 0) {
                    val difference = price - details.entryPrice
                    val color =
                        if (difference >= 0) TradebotTheme.colors.profit
                        else TradebotTheme.colors.loss
                    Text(
                        text =
                            stringResource(
                                R.string.details_price_change,
                                formatMoney(value = difference, signed = true),
                                formatPercent(
                                    value = difference / details.entryPrice * 100,
                                    signed = true,
                                ),
                            ),
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                    )
                    Text(
                        text = stringResource(R.string.details_from_entry),
                        style = MaterialTheme.typography.bodySmall,
                        color = TradebotTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
