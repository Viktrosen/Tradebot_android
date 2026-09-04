package ru.bolotov.feature.main.presentation.core

import androidx.compose.material3.SnackbarDuration

internal data class UiState(
    val currentSection: Any? = null,
    val hasUnreadNotifications: Boolean = false,
    val avatarUrl: String? = null,
)

internal sealed interface Action {
    data class UpdateCurrentSection(val route: Any?) : Action
}

internal sealed interface Effect {
    data class ShowSnackbarMessage(
        val message: String,
        val iconResName: String?,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val action: () -> Unit = {}
    ) : Effect
}