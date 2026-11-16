package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import ru.bolotov.core.uikit.format.formatExplanationNumbers
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotCard
import ru.bolotov.core.uikit.components.TradebotIcon
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun PositionExplanationCard(
    title: String,
    subtitle: String,
    explanation: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    TradebotCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(all = TradebotDimens.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TradebotIcon(resource = UiR.drawable.ic_info_outline)
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
            TradebotIcon(
                resource = R.drawable.ic_details_chevron_down,
                modifier = Modifier.rotate(degrees = if (expanded) 180f else 0f),
            )
        }
        if (expanded) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = TradebotDimens.cardPadding),
                color = TradebotTheme.colors.border,
            )
            Text(
                text = explanation.formatExplanationNumbers(),
                modifier = Modifier.padding(all = TradebotDimens.cardPadding),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
