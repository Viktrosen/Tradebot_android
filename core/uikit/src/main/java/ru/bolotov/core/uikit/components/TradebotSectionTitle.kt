package ru.bolotov.core.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens

@Composable
fun TradebotSectionTitle(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
    ) {
        Text(
            title.uppercase(),
            style = TradebotTheme.typography.section,
            color = TradebotTheme.colors.textSecondary,
        )
        HorizontalDivider(Modifier.weight(1f), color = TradebotTheme.colors.border)
    }
}
