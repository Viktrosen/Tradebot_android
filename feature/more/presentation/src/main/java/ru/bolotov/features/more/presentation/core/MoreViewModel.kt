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
import ru.bolotov.feature.auth.domain.AuthInteractor
import ru.bolotov.feature.auth.domain.AuthSession
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@Stable
internal class MoreViewModel @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val auth: AuthInteractor,
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
            reduce {
                state.copy(
                    isLoading = false,
                    email = (auth.session.value as? AuthSession.SignedIn)?.email.orEmpty(),
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
            "logout" -> logout()
        }
    }

    private fun logout() = intent {
        try { auth.logout() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { postSideEffect(Effect.ShowError("Не удалось завершить выход")) }
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
