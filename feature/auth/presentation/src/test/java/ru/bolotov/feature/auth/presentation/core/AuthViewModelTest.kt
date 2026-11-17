package ru.bolotov.feature.auth.presentation.core

import androidx.lifecycle.ViewModelStore
import ru.bolotov.core.utils.testing.controlOrbit
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.bolotov.feature.auth.domain.exception.LoginRejected
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.model.AuthSession

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @Test
    fun `invalid credentials do not call interactor and editing clears validation error`() = runTest {
        withModel { model, auth ->
            model.handle(Action.Login)
            runCurrent()
            assertNotNull(model.container.stateFlow.value.error)
            assertTrue(auth.logins.isEmpty())
            model.handle(Action.Email(value = "invalid-address"))
            model.handle(Action.Password(value = "test-password"))
            runCurrent()
            assertNull(model.container.stateFlow.value.error)
            model.handle(Action.Login)
            runCurrent()
            assertNotNull(model.container.stateFlow.value.error)
            assertTrue(auth.logins.isEmpty())
        }
    }

    @Test
    fun `login forwards entered credentials and clears password after success`() = runTest {
        withModel { model, auth ->
            enterCredentials(model)
            model.handle(Action.Login)
            runCurrent()
            assertEquals(listOf("owner@example.test" to "test-password"), auth.logins)
            assertEquals(AuthSession.SignedIn(email = "owner@example.test", sessionId = "session"), model.container.stateFlow.value.session)
            assertEquals("", model.container.stateFlow.value.password)
            assertFalse(model.container.stateFlow.value.loading)
            assertNull(model.container.stateFlow.value.error)
        }
    }

    @Test
    fun `rejected credentials show login error and do not retain the password`() = runTest {
        withModel { model, auth ->
            auth.loginFailure = LoginRejected()
            enterCredentials(model)
            model.handle(Action.Login)
            runCurrent()
            assertEquals("Неверная почта или пароль", model.container.stateFlow.value.error)
            assertEquals("", model.container.stateFlow.value.password)
            assertEquals(AuthSession.SignedOut, model.container.stateFlow.value.session)
            assertFalse(model.container.stateFlow.value.loading)
        }
    }

    @Test
    fun `transport failure is not presented as rejected credentials`() = runTest {
        withModel { model, auth ->
            auth.loginFailure = IOException("test unavailable")
            enterCredentials(model)
            model.handle(Action.Login)
            runCurrent()
            assertEquals("Не удалось войти. Проверьте соединение и повторите попытку", model.container.stateFlow.value.error)
            assertEquals("", model.container.stateFlow.value.password)
            assertFalse(model.container.stateFlow.value.loading)
        }
    }

    @Test
    fun `pending login exposes loading and ignores repeated login actions`() = runTest {
        withModel { model, auth ->
            val gate = CompletableDeferred<Unit>()
            auth.loginGate = gate
            enterCredentials(model)
            model.handle(Action.Login)
            runCurrent()
            assertTrue(model.container.stateFlow.value.loading)
            model.handle(Action.Login)
            runCurrent()
            assertEquals(1, auth.logins.size)
            gate.complete(Unit)
            runCurrent()
            assertFalse(model.container.stateFlow.value.loading)
            assertEquals("", model.container.stateFlow.value.password)
        }
    }

    @Test
    fun `external sign in clears the password and retry logout use the interactor`() = runTest {
        withModel { model, auth ->
            model.handle(Action.Password(value = "test-password"))
            runCurrent()
            auth.session.value = AuthSession.SignedIn(email = "owner@example.test", sessionId = "restored")
            runCurrent()
            assertEquals("", model.container.stateFlow.value.password)
            model.handle(Action.Retry)
            model.handle(Action.Logout)
            runCurrent()
            assertEquals(1, auth.restores)
            assertEquals(1, auth.logouts)
            assertEquals(AuthSession.SignedOut, model.container.stateFlow.value.session)
        }
    }

    @Test
    fun `login cancellation clears transient credentials without showing a login error`() = runTest {
        withModel { model, auth ->
            auth.loginFailure = CancellationException("test cancellation")
            enterCredentials(model)
            model.handle(Action.Login)
            runCurrent()
            assertFalse(model.container.stateFlow.value.loading)
            assertEquals("", model.container.stateFlow.value.password)
            assertNull(model.container.stateFlow.value.error)
        }
    }

    private fun enterCredentials(model: AuthViewModel) {
        model.handle(Action.Email(value = "owner@example.test"))
        model.handle(Action.Password(value = "test-password"))
    }

    private suspend fun TestScope.withModel(block: suspend TestScope.(AuthViewModel, FakeAuth) -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val auth = FakeAuth()
            val model = AuthViewModel(auth = auth)
            store.put("auth", model)
            controlOrbit(model)
            block(model, auth)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    private class FakeAuth : AuthInteractor {
        override val session = MutableStateFlow<AuthSession>(AuthSession.SignedOut)
        val logins = mutableListOf<Pair<String, String>>()
        var restores = 0
        var logouts = 0
        var loginFailure: Exception? = null
        var loginGate: CompletableDeferred<Unit>? = null

        override suspend fun restore() { restores++ }

        override suspend fun login(email: String, password: String) {
            logins += email to password
            loginFailure?.let { throw it }
            loginGate?.await()
            session.value = AuthSession.SignedIn(email = email, sessionId = "session")
        }

        override suspend fun logout() {
            logouts++
            session.value = AuthSession.SignedOut
        }
    }
}
