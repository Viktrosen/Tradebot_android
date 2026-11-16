package ru.bolotov.tradebot.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.tradebot.navigation.data.AppLink
import ru.bolotov.tradebot.navigation.data.AppNavigationState
import ru.bolotov.tradebot.navigation.data.PendingAppLink
import ru.bolotov.tradebot.navigation.data.toAppNavigationState

/**
 * Restores a session once per application host; AppNavigationHost owns navigation, data owns
 * tokens.
 */
class AppViewModel
@Inject
constructor(
    auth: AuthInteractor,
    themeInteractor: ThemeInteractor,
    private val savedStateHandle: SavedStateHandle,
    private val json: Json,
) : ViewModel() {
    // Null means the initial disk read is pending, not a default dark preference.
    val themeMode =
        themeInteractor.themeMode
            .map<AppThemeMode, AppThemeMode?> { it }
            .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    // Retained on rotation, renewed after process death. Do not restore an unchecked Main back
    // stack.
    val navigationInstanceId: String = UUID.randomUUID().toString()
    private val pendingLinkJson =
        savedStateHandle.getStateFlow<String?>(key = PENDING_LINK, initialValue = null)
    val navigationState: StateFlow<AppNavigationState> =
        combine(auth.session, pendingLinkJson) { session, encoded ->
                when (val state = session.toAppNavigationState()) {
                    AppNavigationState.Auth -> state
                    is AppNavigationState.Main -> state.copy(pendingLink = decodeLink(encoded))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AppNavigationState.Auth,
            )

    init {
        viewModelScope.launch { auth.restore() }
    }

    fun acceptLink(destination: AppLink) {
        savedStateHandle[PENDING_LINK] =
            json.encodeToString(
                PendingAppLink(requestId = UUID.randomUUID().toString(), destination = destination)
            )
    }

    fun acknowledgeLink(requestId: String) {
        val current = decodeLink(savedStateHandle[PENDING_LINK]) ?: return
        if (current.requestId == requestId) savedStateHandle[PENDING_LINK] = null
    }

    private fun decodeLink(encoded: String?): PendingAppLink? =
        encoded?.let { runCatching { json.decodeFromString<PendingAppLink>(it) }.getOrNull() }

    private companion object {
        const val PENDING_LINK = "navigation.pendingAppLink"
    }
}
