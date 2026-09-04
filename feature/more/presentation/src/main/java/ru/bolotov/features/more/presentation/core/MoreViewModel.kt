package ru.bolotov.features.more.presentation.core

import androidx.compose.runtime.Stable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor
import ru.bolotov.tradebot.feature.more.presentation.R
import javax.inject.Inject

@Stable
internal class MoreViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val emergencyCloseInteractor: EmergencyCloseInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {

    override val container: Container<UiState, Effect> = container(UiState())

    init {
        loadData()
    }

    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadData -> loadData()
            is Action.Refresh -> refresh()
            is Action.ShowApiKeyDialog -> showApiKeyDialog()
            is Action.SaveApiKey -> saveApiKey(action.key)
            is Action.DismissApiKeyDialog -> dismissApiKeyDialog()
            is Action.SetThemeMode -> setThemeMode(action.mode)
            is Action.SetNotificationsEnabled -> setNotificationsEnabled(action.enabled)
            is Action.ShowEmergencyCloseDialog -> showEmergencyCloseDialog()
            is Action.ConfirmEmergencyClose -> confirmEmergencyClose()
            is Action.DismissEmergencyCloseDialog -> dismissEmergencyCloseDialog()
            is Action.NavigateTo -> navigateTo(action.itemId)
            is Action.Logout -> logout()
        }
    }

    private fun loadData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            // TODO: Загрузить из DataStore
            // val apiKey = preferencesRepository.getApiKey()
            // val themeMode = preferencesRepository.getThemeMode()
            // val notificationsEnabled = preferencesRepository.getNotificationsEnabled()

            // Mock data
            reduce {
                state.copy(
                    isLoading = false,
                    apiKey = "••••••••••••••••",
                    isApiKeySaved = true,
                    themeMode = ThemeMode.SYSTEM,
                    notificationsEnabled = true,
                    appVersion = getAppVersion(),
                    buildNumber = getBuildNumber(),
                    errorMessage = null
                )
            }

            analyticManager.logEvent("more_screen_opened")
        } catch (e: Exception) {
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Ошибка загрузки настроек"
                )
            }
            postSideEffect(Effect.ShowError(e.message ?: "Ошибка загрузки настроек"))
        }
    }

    private fun refresh() = intent {
        loadData()
    }

    private fun showApiKeyDialog() = intent {
        postSideEffect(Effect.ShowApiKeyDialog(state.apiKey))
    }

    private fun saveApiKey(key: String) = intent {
        try {
            // TODO: Сохранить API ключ в DataStore
            // preferencesRepository.saveApiKey(key)

            reduce {
                state.copy(
                    apiKey = "••••••••••••••••",
                    isApiKeySaved = true
                )
            }

            postSideEffect(Effect.ShowToast("API ключ сохранён"))
            analyticManager.logEvent("api_key_saved")
        } catch (e: Exception) {
            postSideEffect(Effect.ShowError(e.message ?: "Ошибка сохранения API ключа"))
        }
        postSideEffect(Effect.DismissApiKeyDialog)
    }

    private fun dismissApiKeyDialog() = intent {
        postSideEffect(Effect.DismissApiKeyDialog)
    }

    private fun setThemeMode(mode: ThemeMode) = intent {
        // TODO: Сохранить тему в DataStore
        // preferencesRepository.saveThemeMode(mode)

        reduce {
            state.copy(themeMode = mode)
        }

        postSideEffect(Effect.ShowToast("Тема изменена на ${getThemeName(mode)}"))
        analyticManager.logEvent("theme_changed", bundleOf("theme" to mode.name))
    }

    private fun setNotificationsEnabled(enabled: Boolean) = intent {
        // TODO: Сохранить настройку в DataStore
        // preferencesRepository.saveNotificationsEnabled(enabled)

        reduce {
            state.copy(notificationsEnabled = enabled)
        }

        postSideEffect(Effect.ShowToast(if (enabled) "Уведомления включены" else "Уведомления выключены"))
        analyticManager.logEvent("notifications_toggled", bundleOf("enabled" to enabled))
    }

    private fun showEmergencyCloseDialog() = intent {
        reduce { state.copy(isEmergencyCloseDialogVisible = true) }
    }

    private fun dismissEmergencyCloseDialog() = intent {
        reduce { state.copy(isEmergencyCloseDialogVisible = false) }
    }

    private fun confirmEmergencyClose() = intent {
        reduce {
            state.copy(
                isEmergencyCloseDialogVisible = false,
                isEmergencyCloseInProgress = true
            )
        }

        try {
            emergencyCloseInteractor.closeAllPositions()
            analyticManager.logEvent("emergency_close_requested")
            postSideEffect(Effect.ShowMessage(R.string.emergency_close_started))
        } catch (error: Exception) {
            postSideEffect(Effect.ShowMessage(R.string.emergency_close_failed))
        } finally {
            reduce { state.copy(isEmergencyCloseInProgress = false) }
        }
    }

    private fun navigateTo(itemId: String) = intent {
        when (itemId) {
            "instruments" -> {
                analyticManager.logEvent("navigate_to_instruments")
                postSideEffect(Effect.NavigateToInstruments)
            }
            "history" -> {
                analyticManager.logEvent("navigate_to_history")
                postSideEffect(Effect.NavigateToHistory)
            }
            "settings" -> {
                analyticManager.logEvent("navigate_to_settings")
                postSideEffect(Effect.NavigateToSettings)
            }
            "about" -> {
                analyticManager.logEvent("navigate_to_about")
                postSideEffect(Effect.NavigateToAbout)
            }
            "logout" -> {
                analyticManager.logEvent("logout_attempt")
                postSideEffect(Effect.Logout)
            }
        }
    }

    private fun logout() = intent {
        // TODO: Очистить DataStore, API ключ и т.д.
        // preferencesRepository.clear()

        postSideEffect(Effect.ShowToast("Выход выполнен"))
        analyticManager.logEvent("logout_success")
        postSideEffect(Effect.Logout)
    }

    private fun getAppVersion(): String {
        // TODO: Получить из BuildConfig
        return "1.0.0"
    }

    private fun getBuildNumber(): String {
        // TODO: Получить из BuildConfig
        return "1"
    }

    private fun getThemeName(mode: ThemeMode): String {
        return when (mode) {
            ThemeMode.LIGHT -> "Светлая"
            ThemeMode.DARK -> "Тёмная"
            ThemeMode.SYSTEM -> "Системная"
        }
    }
}
