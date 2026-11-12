package ru.bolotov.core.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme

@Composable
fun TradebotBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TradebotTheme.colors.accent,
) {
    Surface(
        modifier = modifier,
        shape = TradebotTheme.shapes.badge,
        color = color.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.8f)),
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            style = TradebotTheme.typography.label,
        )
    }
}
