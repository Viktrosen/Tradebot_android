package ru.bolotov.feature.risk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotConfirmationDialog
import ru.bolotov.tradebot.feature.risk.presentation.R

@Composable
fun ResetConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    TradebotConfirmationDialog(
        title = stringResource(R.string.risk_reset_dialog_title),
        message = stringResource(R.string.risk_reset_dialog_message),
        confirmText = stringResource(R.string.risk_screen_reset),
        dismissText = stringResource(R.string.risk_dialog_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        destructive = true,
    )
}

@Preview
@Composable
private fun ResetConfirmationDialogPreview() {
    TradebotTheme { ResetConfirmationDialog(onConfirm = {}, onDismiss = {}) }
}
