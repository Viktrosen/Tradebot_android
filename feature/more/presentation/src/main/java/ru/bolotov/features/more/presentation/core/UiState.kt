package ru.bolotov.features.more.presentation.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star


data class UiState(
    val isLoading: Boolean = false,
    val apiKey: String = "",
    val isApiKeySaved: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1",
    val isEmergencyCloseDialogVisible: Boolean = false,
    val isEmergencyCloseInProgress: Boolean = false,
    val errorMessage: String? = null
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

sealed class MoreMenuItem(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badge: String? = null
) {
    object Instruments : MoreMenuItem(
        id = "instruments",
        title = "Инструменты",
        icon = Icons.Default.Star  // ← ShowChart → Star
    )
    object History : MoreMenuItem(
        id = "history",
        title = "История сделок",
        icon = Icons.Default.Favorite  // ← History → Favorite
    )
    object Settings : MoreMenuItem(
        id = "settings",
        title = "Настройки",
        icon = Icons.Default.Settings
    )
    object About : MoreMenuItem(
        id = "about",
        title = "О приложении",
        icon = Icons.Default.Info
    )
    object Logout : MoreMenuItem(
        id = "logout",
        title = "Выход",
        icon = Icons.Default.Close  // ← Logout → Close
    )
}

sealed interface Effect {
    data class ShowToast(val message: String) : Effect
    data class ShowMessage(val messageResId: Int) : Effect
    data class ShowError(val error: String) : Effect
    data class ShowApiKeyDialog(val currentKey: String) : Effect
    data object DismissApiKeyDialog : Effect
    data object NavigateToInstruments : Effect
    data object NavigateToHistory : Effect
    data object NavigateToSettings : Effect
    data object NavigateToAbout : Effect
    data object Logout : Effect
}

sealed interface Action {
    data object LoadData : Action
    data object Refresh : Action

    // API Key
    data object ShowApiKeyDialog : Action
    data class SaveApiKey(val key: String) : Action
    data object DismissApiKeyDialog : Action

    // Theme
    data class SetThemeMode(val mode: ThemeMode) : Action

    // Notifications
    data class SetNotificationsEnabled(val enabled: Boolean) : Action

    data object ShowEmergencyCloseDialog : Action
    data object ConfirmEmergencyClose : Action
    data object DismissEmergencyCloseDialog : Action

    // Navigation
    data class NavigateTo(val itemId: String) : Action
    data object Logout : Action
}
