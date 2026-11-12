package ru.bolotov.core.uikit.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme

@Composable
fun TradebotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
    destructive: Boolean = false,
    @DrawableRes iconRes: Int? = null,
) {
    val colors = TradebotTheme.colors
    val accent = if (destructive) colors.loss else colors.accent
    val content: @Composable RowScope.() -> Unit = {
        iconRes?.let {
            TradebotIcon(it, tint = LocalContentColor.current)
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
    val buttonModifier = modifier.defaultMinSize(minHeight = 48.dp)
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            border = BorderStroke(1.dp, if (enabled) accent else colors.border),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = accent,
                    disabledContentColor = colors.textSecondary,
                ),
        ) {
            content()
        }
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = colors.onAccent,
                    disabledContainerColor = colors.surfaceRaised,
                    disabledContentColor = colors.textSecondary,
                ),
        ) {
            content()
        }
    }
}
