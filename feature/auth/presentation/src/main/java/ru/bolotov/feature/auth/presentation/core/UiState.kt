package ru.bolotov.feature.auth.presentation.core

import ru.bolotov.feature.auth.domain.model.AuthSession

internal data class UiState(
    val session: AuthSession = AuthSession.Checking,
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

internal sealed interface Action {
    data class Email(val value: String) : Action
    data class Password(val value: String) : Action
    data object Login : Action
    data object Retry : Action
    data object Logout : Action
}
