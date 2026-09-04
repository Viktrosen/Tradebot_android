package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun CandlestickConfigDialog(
    initialTimeframe: String = "M15",
    initialMinConfidence: Double = 0.85,
    onConfirm: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTimeframe by remember(initialTimeframe) { mutableStateOf(initialTimeframe) }
    var minConfidence by remember(initialMinConfidence) { mutableStateOf(initialMinConfidence) }

    val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройка свечной стратегии") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timeframe selection
                Column {
                    Text("Таймфрейм:")
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        timeframes.forEach { timeframe ->
                            FilterChip(
                                selected = selectedTimeframe == timeframe,
                                onClick = { selectedTimeframe = timeframe },
                                label = { Text(timeframe) }
                            )
                        }
                    }
                }

                // Confidence slider
                Column {
                    Text("Минимальная уверенность: ${(minConfidence * 100).toInt()}%")
                    Slider(
                        value = minConfidence.toFloat(),
                        onValueChange = { minConfidence = it.toDouble() },
                        valueRange = 0.85f..0.95f,
                        steps = 1
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedTimeframe, minConfidence) }
            ) {
                Text("Применить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}
