package ru.bolotov.core.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.tradebot.core.uikit.R

/** Header used in Scaffold.topBar; it does not own refreshing or navigation state. */
@Composable
fun TradebotHeader(title: String, subtitle: String, onBack: (() -> Unit)? = null) {
    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = TradebotDimens.screenPadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val brand = stringResource(R.string.tb_brand)
        Text(
            buildAnnotatedString {
                append(brand.dropLast(1))
                withStyle(SpanStyle(color = TradebotTheme.colors.brand)) { append(brand.last()) }
            },
            style = TradebotTheme.typography.section,
            color = TradebotTheme.colors.textSecondary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    TradebotIcon(
                        R.drawable.ic_arrow_left_outline_24,
                        contentDescription = stringResource(R.string.tb_back),
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = TradebotTheme.typography.screenTitle)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TradebotTheme.colors.textSecondary,
                )
            }
            TradebotIcon(
                R.drawable.ic_tradebot_mark,
                Modifier.size(42.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified,
            )
        }
    }
}
