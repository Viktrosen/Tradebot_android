package ru.bolotov.core.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens

@Composable
fun TradebotMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = TradebotTheme.colors.text,
) {
    TradebotCard(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(TradebotDimens.itemGap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
                maxLines = 1,
            )
            Text(value, style = MaterialTheme.typography.titleLarge, color = color, maxLines = 1)
        }
    }
}
