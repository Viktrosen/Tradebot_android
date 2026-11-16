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
internal fun PositionProtectionCard(details: PositionDetailsUiModel) {
    TradebotCard(modifier = Modifier.fillMaxWidth(), shape = TradebotTheme.shapes.clipped) {
        Column(
            modifier = Modifier.padding(all = TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            PositionDetailRow(
                label = stringResource(R.string.details_broker_stop),
                value = detailsMoney(details.brokerStopLossPrice),
            )
            HorizontalDivider(color = TradebotTheme.colors.border)
            PositionDetailRow(
                label = stringResource(R.string.details_profit_protection),
                value =
                    if (
                        details.managedExitPrice == null &&
                            details.profitProtectionStage == "INACTIVE"
                    ) {
                        stringResource(R.string.details_protection_inactive)
                    } else detailsMoney(details.managedExitPrice),
            )
            Text(
                text = stringResource(R.string.details_bot_exit),
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
            )
            HorizontalDivider(color = TradebotTheme.colors.border)
            PositionDetailRow(
                label = stringResource(R.string.details_take_profit),
                value = detailsMoney(details.takeProfitPrice),
            )
        }
    }
}
