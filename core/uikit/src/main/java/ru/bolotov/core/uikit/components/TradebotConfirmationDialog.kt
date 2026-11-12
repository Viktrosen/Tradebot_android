package ru.bolotov.core.uikit.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.tradebot.core.uikit.R

/** Visual confirmation only: never initiates an action on dismissal. */
@Composable
fun TradebotConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
    properties: DialogProperties = DialogProperties(),
) {
    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        destructive = destructive,
        confirmButton = {
            TradebotDialogButton(onClick = onConfirm, destructive = destructive) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TradebotDialogButton(onClick = onDismiss, outlined = true) { Text(dismissText) }
        },
        properties = properties,
    )
}

@Preview(name = "Confirmation")
@Composable
private fun TradebotConfirmationDialogPreview() {
    TradebotTheme {
        TradebotConfirmationDialog(
            title = stringResource(R.string.tb_dialog_preview_title),
            message = stringResource(R.string.tb_dialog_preview_message),
            confirmText = stringResource(R.string.tb_dialog_confirm),
            dismissText = stringResource(R.string.tb_dialog_cancel),
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Destructive confirmation", fontScale = 1.3f)
@Composable
private fun TradebotDestructiveConfirmationDialogPreview() {
    TradebotTheme {
        TradebotConfirmationDialog(
            title = stringResource(R.string.tb_dialog_preview_title),
            message = stringResource(R.string.tb_dialog_preview_message),
            confirmText = stringResource(R.string.tb_dialog_confirm),
            dismissText = stringResource(R.string.tb_dialog_cancel),
            onConfirm = {},
            onDismiss = {},
            destructive = true,
        )
    }
}
