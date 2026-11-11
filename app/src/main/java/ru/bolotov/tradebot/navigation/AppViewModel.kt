package ru.bolotov.tradebot.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.bolotov.feature.auth.domain.AuthInteractor
import java.util.UUID
import javax.inject.Inject

/** Restores a session once per application host; Activity owns navigation, data owns tokens. */
class AppViewModel @Inject constructor(auth: AuthInteractor) : ViewModel() {
    // Retained on rotation, renewed after process death. Do not restore an unchecked Main back stack.
    val navigationInstanceId: String = UUID.randomUUID().toString()
    val navigationState = auth.session.map { it.toAppNavigationState() }.distinctUntilChanged()

    init {
        viewModelScope.launch { auth.restore() }
    }
}
