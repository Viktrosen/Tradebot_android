package ru.bolotov.feature.splashScreen.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

internal class SplashViewModel @Inject constructor() : ViewModel(), ContainerHost<Unit, Effect> {

    override val container: Container<Unit, Effect> = container(Unit)

    fun handleAction(action: Action) = when (action) {
        is Action.OnNavigateToMainScreen -> navigateToMainScreen()
    }

    private fun navigateToMainScreen() = intent {
        postSideEffect(Effect.NavigateMainScreen)
    }
}
