package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotIcon
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.positionDetails.presentation.core.CloseUiState
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.positionDetails.presentation.R

@Composable
internal fun PositionDetailsBottomBar(closeState: CloseUiState, onClose: () -> Unit) {
    val busy = closeState != CloseUiState.Idle && closeState != CloseUiState.Confirming
    Surface(color = TradebotTheme.colors.background) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(all = TradebotDimens.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
        ) {
            OutlinedButton(
                onClick = onClose,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = TradebotTheme.shapes.material.small,
                border =
                    BorderStroke(
                        width = TradebotDimens.border,
                        color = if (busy) TradebotTheme.colors.border else TradebotTheme.colors.loss,
                    ),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = TradebotTheme.colors.loss,
                        disabledContentColor = TradebotTheme.colors.textSecondary,
                    ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(TradebotDimens.compactGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (busy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = TradebotDimens.icon),
                            strokeWidth = 2.dp,
                            color = TradebotTheme.colors.textSecondary,
                        )
                    } else {
                        TradebotIcon(
                            resource = UiR.drawable.ic_warning_outline,
                            tint = TradebotTheme.colors.loss,
                        )
                    }
                    Text(
                        text =
                            stringResource(
                                if (busy) R.string.details_closing_action
                                else R.string.details_close_action
                            ),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Text(
                text =
                    stringResource(
                        if (busy) R.string.details_waiting_close
                        else R.string.details_confirmation_hint
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = TradebotTheme.colors.textSecondary,
            )
        }
    }
}
