package ru.bolotov.feature.auth.domain.model

/** Session identity only: credentials and tokens never cross into presentation. */
sealed interface AuthSession {
    data object Checking : AuthSession
    data object SignedOut : AuthSession
    data object Unavailable : AuthSession
    data class SignedIn(val email: String, val sessionId: String) : AuthSession
}
