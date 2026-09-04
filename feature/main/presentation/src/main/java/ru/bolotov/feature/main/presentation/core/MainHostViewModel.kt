package ru.bolotov.feature.main.presentation.core

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

internal class MainHostViewModel @Inject constructor(
) : ViewModel(), ContainerHost<UiState, Effect> {

    override val container: Container<UiState, Effect> = container(UiState())

    fun handleAction(action: Action) {
        when (action) {
            is Action.UpdateCurrentSection -> updateCurrentSection(action)
        }
    }

    private fun updateCurrentSection(action: Action.UpdateCurrentSection) = intent {
        reduce {
            state.copy(currentSection = action.route)
        }
    }
}