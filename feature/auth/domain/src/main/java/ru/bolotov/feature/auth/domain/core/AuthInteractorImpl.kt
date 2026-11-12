package ru.bolotov.feature.auth.domain.core

import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.repository.AuthRepository

class AuthInteractorImpl(private val repository: AuthRepository) : AuthInteractor {
    override val session = repository.session
    override suspend fun restore() = repository.restore()
    override suspend fun login(email: String, password: String) = repository.login(email.trim(), password)
    override suspend fun logout() = repository.logout()
}
