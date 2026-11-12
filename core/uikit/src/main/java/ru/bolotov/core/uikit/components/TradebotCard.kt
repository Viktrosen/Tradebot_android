package ru.bolotov.core.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens

/** Shared panel. Content owns its padding; the outline follows the actual shape. */
@Composable
fun TradebotCard(
    modifier: Modifier = Modifier,
    shape: Shape = TradebotTheme.shapes.card,
    containerColor: Color = TradebotTheme.colors.surface,
    borderColor: Color = TradebotTheme.colors.border,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(TradebotDimens.border, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        content = content,
    )
}
