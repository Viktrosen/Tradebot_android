package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotConfirmationDialog
import ru.bolotov.tradebot.feature.positions.presentation.R

@Composable
fun ClosePositionDialog(positionName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    TradebotConfirmationDialog(
        title = stringResource(R.string.positions_close_title),
        message = stringResource(R.string.positions_close_message, positionName),
        confirmText = stringResource(R.string.position_close),
        dismissText = stringResource(R.string.positions_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        destructive = true,
    )
}

@Preview
@Composable
private fun ClosePositionDialogPreview() {
    TradebotTheme { ClosePositionDialog(positionName = "SBER", onConfirm = {}, onDismiss = {}) }
}
