package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotConfirmationDialog
import ru.bolotov.tradebot.feature.strategy.presentation.R

@Composable
fun SimpleConfigDialog(strategyType: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    TradebotConfirmationDialog(
        title = stringResource(R.string.strategies_simple_title),
        message = stringResource(R.string.strategies_simple_message, strategyType),
        confirmText = stringResource(R.string.strategies_yes),
        dismissText = stringResource(R.string.strategies_cancel),
        onConfirm = { onConfirm(strategyType) },
        onDismiss = onDismiss,
        destructive = false,
    )
}

@Preview
@Composable
private fun SimpleConfigDialogPreview() {
    TradebotTheme { SimpleConfigDialog(strategyType = "EMA", onConfirm = {}, onDismiss = {}) }
}
