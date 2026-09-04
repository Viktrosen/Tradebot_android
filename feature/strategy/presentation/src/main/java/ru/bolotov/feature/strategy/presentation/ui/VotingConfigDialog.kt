package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun VotingConfigDialog(
    initialWeights: Map<String, Int> = mapOf("EMA" to 1, "RSI" to 1, "MACD" to 1, "BB" to 1),
    onConfirm: (Map<String, Int>) -> Unit,
    onDismiss: () -> Unit
) {
    var emaWeight by remember(initialWeights) { mutableStateOf(initialWeights["EMA"] ?: 1) }
    var rsiWeight by remember(initialWeights) { mutableStateOf(initialWeights["RSI"] ?: 1) }
    var macdWeight by remember(initialWeights) { mutableStateOf(initialWeights["MACD"] ?: 1) }
    var bbWeight by remember(initialWeights) { mutableStateOf(initialWeights["BB"] ?: 1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройка стратегии голосования") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WeightSliderRow("EMA", value = emaWeight, onValueChange = { emaWeight = it })
                WeightSliderRow("RSI", value = rsiWeight, onValueChange = { rsiWeight = it })
                WeightSliderRow("MACD", value = macdWeight, onValueChange = { macdWeight = it })
                WeightSliderRow("BB", value = bbWeight, onValueChange = { bbWeight = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Суммарный вес: ${emaWeight + rsiWeight + macdWeight + bbWeight}",
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weights = mapOf(
                        "EMA" to emaWeight,
                        "RSI" to rsiWeight,
                        "MACD" to macdWeight,
                        "BB" to bbWeight
                    )
                    onConfirm(weights)
                }
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

@Composable
private fun WeightSliderRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.width(50.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.weight(1f)
        )
        Text(text = value.toString(), modifier = Modifier.width(30.dp))
    }
}
