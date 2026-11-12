package ru.bolotov.feature.auth.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.feature.auth.domain.exception.LoginRejected
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.model.AuthSession

internal class AuthViewModel @Inject constructor(private val auth: AuthInteractor) : ViewModel(), ContainerHost<UiState, Nothing> {
    override val container = container<UiState, Nothing>(UiState(session = auth.session.value))
    init {
        viewModelScope.launch {
            auth.session.collect { session ->
                intent { reduce { state.copy(session = session, password = if (session is AuthSession.SignedIn) "" else state.password) } }
            }
        }
    }

    fun handle(action: Action) {
        when (action) {
            is Action.Email -> intent { reduce { state.copy(email = action.value, error = null) } }
            is Action.Password -> intent { reduce { state.copy(password = action.value, error = null) } }
            Action.Login -> login()
            Action.Retry -> restore()
            Action.Logout -> viewModelScope.launch { auth.logout() }
        }
    }

    private fun restore() { viewModelScope.launch { auth.restore() } }

    private fun login() = intent {
        if (state.loading) return@intent
        if (state.email.isBlank() || !state.email.contains('@') || state.password.isBlank()) {
            reduce { state.copy(error = "Введите почту и пароль") }
            return@intent
        }
        reduce { state.copy(loading = true, error = null) }
        try { auth.login(state.email, state.password) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: LoginRejected) { reduce { state.copy(error = "Неверная почта или пароль") } }
        catch (_: Exception) { reduce { state.copy(error = "Не удалось войти. Проверьте соединение и повторите попытку") } }
        finally { reduce { state.copy(loading = false, password = "") } }
    }
}
