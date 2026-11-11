package ru.bolotov.tradebot.navigation

import ru.bolotov.feature.auth.domain.AuthSession

sealed interface AppNavigationState {
    data object Auth : AppNavigationState
    data class Main(val sessionId: String) : AppNavigationState
}

/** Checking/offline restore stays on Auth, where the feature displays progress/retry. */
internal fun AuthSession.toAppNavigationState(): AppNavigationState = when (this) {
    is AuthSession.SignedIn -> AppNavigationState.Main(sessionId)
    AuthSession.Checking, AuthSession.SignedOut, AuthSession.Unavailable -> AppNavigationState.Auth
}

/** Re-collecting on resume must not reset Main; a different login must never reuse its screens. */
internal fun AppNavigationState.requiresNavigation(
    isAuthDestination: Boolean,
    isMainDestination: Boolean,
    displayedSessionId: String?
): Boolean = when (this) {
    AppNavigationState.Auth -> !isAuthDestination
    is AppNavigationState.Main -> !isMainDestination || displayedSessionId != sessionId
}
