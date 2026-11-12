package ru.bolotov.feature.auth.domain.interactor

import kotlinx.coroutines.flow.StateFlow
import ru.bolotov.feature.auth.domain.model.AuthSession

interface AuthInteractor {
    val session: StateFlow<AuthSession>
    suspend fun restore()
    suspend fun login(email: String, password: String)
    suspend fun logout()
}
