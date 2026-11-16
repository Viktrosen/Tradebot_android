package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.bolotov.core.uikit.components.TradebotConfirmationDialog
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun ClosePositionConfirmationDialog(
    details: PositionDetailsUiModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TradebotConfirmationDialog(
        title = stringResource(R.string.details_close_title),
        message =
            stringResource(
                R.string.details_close_message,
                details.instrumentName,
                details.positionSide,
                (details.quantity.toBigInteger() * details.lotSize.toBigInteger()).toString(),
            ),
        confirmText = stringResource(R.string.details_confirm_close),
        dismissText = stringResource(R.string.details_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        destructive = true,
    )
}
