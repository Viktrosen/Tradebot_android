package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotDialog
import ru.bolotov.core.uikit.components.TradebotDialogButton
import ru.bolotov.core.uikit.components.TradebotSlider
import ru.bolotov.tradebot.feature.strategy.presentation.R

@Composable
fun CandlestickConfigDialog(
    initialTimeframe: String = "M15",
    initialMinConfidence: Double = 0.85,
    onConfirm: (String, Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedTimeframe by remember(initialTimeframe) { mutableStateOf(initialTimeframe) }
    var minConfidence by remember(initialMinConfidence) { mutableStateOf(initialMinConfidence) }

    val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4")

    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.strategies_candlestick_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timeframe selection
                Column {
                    Text(stringResource(R.string.strategies_timeframe))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    ) {
                        timeframes.forEach { timeframe ->
                            FilterChip(
                                selected = selectedTimeframe == timeframe,
                                colors =
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TradebotTheme.colors.accent,
                                        selectedLabelColor = TradebotTheme.colors.onAccent,
                                    ),
                                onClick = { selectedTimeframe = timeframe },
                                label = { Text(timeframe) },
                            )
                        }
                    }
                }

                // Confidence slider
                Column {
                    Text(
                        stringResource(
                            R.string.strategies_min_confidence,
                            (minConfidence * 100).toInt(),
                        )
                    )
                    TradebotSlider(
                        value = minConfidence.toFloat(),
                        onValueChange = { minConfidence = it.toDouble() },
                        valueRange = 0.85f..0.95f,
                        steps = 1,
                    )
                }
            }
        },
        confirmButton = {
            TradebotDialogButton(onClick = { onConfirm(selectedTimeframe, minConfidence) }) {
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
private fun CandlestickConfigDialogPreview() {
    TradebotTheme { CandlestickConfigDialog(onConfirm = { _, _ -> }, onDismiss = {}) }
}
