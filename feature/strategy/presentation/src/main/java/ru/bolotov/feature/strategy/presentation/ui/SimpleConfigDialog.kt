package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties

@Composable
fun SimpleConfigDialog(
    strategyType: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройка простой стратегии") },
        text = { Text("Вы уверены, что хотите переключиться на $strategyType?") },
        confirmButton = {
            TextButton(onClick = { onConfirm(strategyType) }) {
                Text("Да")
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