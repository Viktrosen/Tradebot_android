package ru.bolotov.core.uikit.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.tradebot.core.uikit.R

@Composable
fun TradebotNavigationRow(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    TradebotCard(modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.padding(TradebotDimens.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap),
        ) {
            TradebotIcon(icon)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                subtitle?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TradebotTheme.colors.textSecondary,
                    )
                }
            }
            TradebotIcon(R.drawable.ic_chevron_right, tint = TradebotTheme.colors.textSecondary)
        }
    }
}
