package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun PositionOpeningCard(details: PositionDetailsUiModel) {
    TradebotCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(all = TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            Text(
                text =
                    stringResource(
                        if (details.isClosed) R.string.details_history else R.string.details_opening
                    ),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.details_opened_at),
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
            )
            Text(text = detailsTime(details.entryTime), style = MaterialTheme.typography.bodyMedium)
            if (details.isClosed) {
                Text(
                    text = stringResource(R.string.details_closed_at),
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
                Text(
                    text = detailsTime(details.closeTime),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            details.entryStrategyName
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    HorizontalDivider(color = TradebotTheme.colors.border)
                    PositionDetailRow(label = stringResource(R.string.details_strategy), value = it)
                }
        }
    }
}
