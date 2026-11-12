package ru.bolotov.core.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.tradebot.core.uikit.R

/** Shared dialog chrome; content and callbacks remain owned by the feature. */
@Composable
fun TradebotDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    destructive: Boolean = false,
    properties: DialogProperties = DialogProperties(),
) {
    val colors = TradebotTheme.colors
    val accent = if (destructive) colors.loss else colors.accent
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = TradebotTheme.shapes.clipped,
            color = colors.surface,
            contentColor = colors.text,
            border = BorderStroke(TradebotDimens.border, if (destructive) accent else colors.border),
        ) {
            Column(
                Modifier.heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.85f)
                    .padding(TradebotDimens.sectionGap)
            ) {
                TradebotIcon(
                    if (destructive) R.drawable.ic_warning_outline else R.drawable.ic_info_outline,
                    tint = accent,
                )
                Spacer(Modifier.height(TradebotDimens.itemGap))
                Column(Modifier.semantics { heading() }) {
                    ProvideTextStyle(TradebotTheme.typography.material.titleLarge, title)
                }
                Spacer(Modifier.height(TradebotDimens.itemGap))
                // Only the body scrolls: confirmation and cancellation stay reachable.
                Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                    CompositionLocalProvider(LocalContentColor provides colors.textSecondary) {
                        ProvideTextStyle(TradebotTheme.typography.material.bodyMedium, text)
                    }
                }
                Spacer(Modifier.height(TradebotDimens.sectionGap))
                confirmButton()
                dismissButton?.let {
                    Spacer(Modifier.height(TradebotDimens.compactGap))
                    it()
                }
            }
        }
    }
}
