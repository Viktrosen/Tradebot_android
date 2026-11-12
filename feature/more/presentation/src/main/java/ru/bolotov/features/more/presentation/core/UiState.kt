package ru.bolotov.features.more.presentation.core

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.bolotov.tradebot.core.uikit.R as UiR
import ru.bolotov.tradebot.feature.more.presentation.R

data class UiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1",
    val isEmergencyCloseDialogVisible: Boolean = false,
    val isEmergencyCloseInProgress: Boolean = false,
    val errorMessage: String? = null,
)

sealed class MoreMenuItem(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val badge: String? = null,
) {
    object Instruments :
        MoreMenuItem(
            id = "instruments",
            titleRes = R.string.more_instruments,
            iconRes = UiR.drawable.ic_candles_outline,
        )

    object History :
        MoreMenuItem(
            id = "history",
            titleRes = R.string.more_history,
            iconRes = UiR.drawable.ic_positions_outline,
        )

    object Settings :
        MoreMenuItem(
            id = "settings",
            titleRes = R.string.more_settings,
            iconRes = UiR.drawable.ic_settings_outline,
        )

    object About :
        MoreMenuItem(
            id = "about",
            titleRes = R.string.more_about,
            iconRes = UiR.drawable.ic_info_outline,
        )

    object Logout :
        MoreMenuItem(
            id = "logout",
            titleRes = R.string.more_logout,
            iconRes = UiR.drawable.ic_close_24,
        )
}

sealed interface Effect {
    data class ShowToast(val message: String) : Effect

    data class ShowMessage(val messageResId: Int) : Effect

    data class ShowError(val error: String) : Effect

    data object NavigateToInstruments : Effect

    data object NavigateToHistory : Effect

    data object NavigateToSettings : Effect

    data object NavigateToAbout : Effect

    data object Logout : Effect
}

sealed interface Action {
    data object LoadData : Action

    data object Refresh : Action

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
