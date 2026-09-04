package ru.bolotov.feature.splashScreen.presentation.core

internal sealed interface Action {
    data object OnNavigateToMainScreen : Action
}

internal sealed interface Effect {
    data object NavigateMainScreen : Effect
}