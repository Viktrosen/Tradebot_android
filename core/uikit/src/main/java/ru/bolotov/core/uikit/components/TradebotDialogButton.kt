package ru.bolotov.core.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens

/** Full-width dialog action; stacked actions also accommodate long translated labels. */
@Composable
fun TradebotDialogButton(
    onClick: () -> Unit,
    outlined: Boolean = false,
    destructive: Boolean = false,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = TradebotTheme.colors
    val accent = if (destructive) colors.loss else colors.accent
    val modifier = Modifier.fillMaxWidth().heightIn(min = TradebotDimens.touchTarget)
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            shape = TradebotTheme.shapes.material.small,
            border = BorderStroke(TradebotDimens.border, colors.border),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.text),
            content = content,
        )
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            shape = TradebotTheme.shapes.material.small,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = colors.onAccent,
                ),
            content = content,
        )
    }
}
