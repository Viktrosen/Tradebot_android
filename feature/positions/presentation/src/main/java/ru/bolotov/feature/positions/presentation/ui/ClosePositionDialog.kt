package ru.bolotov.feature.positions.presentation.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun ClosePositionDialog(
    positionName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Закрыть позицию") },
        text = { Text("Вы уверены, что хотите закрыть позицию \"$positionName\"?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Закрыть", color = MaterialTheme.colorScheme.error)
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