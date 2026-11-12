package ru.bolotov.feature.risk.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotBadge
import ru.bolotov.core.uikit.components.TradebotCard
import ru.bolotov.core.uikit.dimensions.TradebotDimens

@Composable
fun RiskCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    value: String,
    unit: String,
    isPercentage: Boolean = false,
    content: @Composable () -> Unit,
) {
    TradebotCard(modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap)) {
                Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                TradebotBadge("$value$unit")
            }
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
            )
            content()
        }
    }
}
