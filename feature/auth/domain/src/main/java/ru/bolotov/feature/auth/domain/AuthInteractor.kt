package ru.bolotov.feature.auth.domain

import kotlinx.coroutines.flow.StateFlow

/** Session identity only: credentials and tokens never cross into presentation. */
sealed interface AuthSession {
    data object Checking : AuthSession
    data object SignedOut : AuthSession
    data object Unavailable : AuthSession
    data class SignedIn(val email: String, val sessionId: String) : AuthSession
}

class LoginRejected : Exception()
class AuthUnavailable : Exception()

interface AuthRepository {
    val session: StateFlow<AuthSession>
    suspend fun restore()
    suspend fun login(email: String, password: String)
    suspend fun logout()
}

interface AuthInteractor {
    val session: StateFlow<AuthSession>
    suspend fun restore()
    suspend fun login(email: String, password: String)
    suspend fun logout()
}

class AuthInteractorImpl(private val repository: AuthRepository) : AuthInteractor {
    override val session = repository.session
    override suspend fun restore() = repository.restore()
    override suspend fun login(email: String, password: String) = repository.login(email.trim(), password)
    override suspend fun logout() = repository.logout()
}
