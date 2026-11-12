package ru.bolotov.tradebot.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.tradebot.navigation.data.toAppNavigationState

/** Restores a session once per application host; Activity owns navigation, data owns tokens. */
class AppViewModel @Inject constructor(auth: AuthInteractor, themeInteractor: ThemeInteractor) :
    ViewModel() {
    // Null means the initial disk read is pending, not a default dark preference.
    val themeMode =
        themeInteractor.themeMode
            .map<AppThemeMode, AppThemeMode?> { it }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Retained on rotation, renewed after process death. Do not restore an unchecked Main back
    // stack.
    val navigationInstanceId: String = UUID.randomUUID().toString()
    val navigationState = auth.session.map { it.toAppNavigationState() }.distinctUntilChanged()

    init {
        viewModelScope.launch { auth.restore() }
    }
}
