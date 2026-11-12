package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotDialog
import ru.bolotov.core.uikit.components.TradebotDialogButton
import ru.bolotov.tradebot.feature.strategy.presentation.R

@Composable
fun ConfirmationConfigDialog(
    initialIndicators: Set<String> = setOf("EMA", "RSI", "MACD", "BB"),
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedIndicators by remember(initialIndicators) { mutableStateOf(initialIndicators) }

    val indicators = listOf("EMA", "RSI", "MACD", "BB")

    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.strategies_confirmation_title)) },
        text = {
            Column {
                Text(stringResource(R.string.strategies_select_indicators))
                Spacer(modifier = Modifier.height(8.dp))

                indicators.forEach { indicator ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = indicator in selectedIndicators,
                            onCheckedChange = { isChecked ->
                                selectedIndicators =
                                    if (isChecked) {
                                        selectedIndicators + indicator
                                    } else {
                                        selectedIndicators - indicator
                                    }
                            },
                        )
                        Text(indicator)
                    }
                }
            }
        },
        confirmButton = {
            TradebotDialogButton(onClick = { onConfirm(selectedIndicators.toList()) }) {
                Text(stringResource(R.string.strategies_apply))
            }
        },
        dismissButton = {
            TradebotDialogButton(onClick = onDismiss, outlined = true) {
                Text(stringResource(R.string.strategies_cancel))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    )
}

@Preview
@Composable
private fun ConfirmationConfigDialogPreview() {
    TradebotTheme { ConfirmationConfigDialog(onConfirm = {}, onDismiss = {}) }
}
