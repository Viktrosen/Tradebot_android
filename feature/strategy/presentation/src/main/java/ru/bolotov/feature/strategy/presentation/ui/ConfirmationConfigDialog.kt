package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationConfigDialog(
    initialIndicators: Set<String> = setOf("EMA", "RSI", "MACD", "BB"),
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIndicators by remember(initialIndicators) { mutableStateOf(initialIndicators) }

    val indicators = listOf("EMA", "RSI", "MACD", "BB")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройка стратегии подтверждения") },
        text = {
            Column {
                Text("Выберите индикаторы для голосования:")
                Spacer(modifier = Modifier.height(8.dp))

                indicators.forEach { indicator ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = indicator in selectedIndicators,
                            onCheckedChange = { isChecked ->
                                selectedIndicators = if (isChecked) {
                                    selectedIndicators + indicator
                                } else {
                                    selectedIndicators - indicator
                                }
                            }
                        )
                        Text(indicator)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedIndicators.toList()) }
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
