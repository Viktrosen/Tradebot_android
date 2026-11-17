package ru.bolotov.features.more.presentation.core

import androidx.lifecycle.ViewModelStore
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.utils.testing.controlOrbit
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.feature.more.domain.interactor.AppInfoInteractor
import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.tradebot.feature.more.presentation.R

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class MoreViewModelTest {
    @Test
    fun `load shows the signed in owner and observed persisted palette`() = runTest {
        withModel { model, _, theme, _ ->
            assertEquals("owner@example.test", model.container.stateFlow.value.email)
            assertEquals(ThemeMode.DARK, model.container.stateFlow.value.themeMode)
            assertFalse(model.container.stateFlow.value.isLoading)
            assertNull(model.container.stateFlow.value.errorMessage)
            theme.themeMode.value = AppThemeMode.LIGHT
            runCurrent()
            assertEquals(ThemeMode.LIGHT, model.container.stateFlow.value.themeMode)
        }
    }

    @Test
    fun `palette selection waits for persisted flow rather than optimistic state`() = runTest {
        withModel { model, _, theme, _ ->
            theme.publishWrites = false
            model.handleAction(Action.SetThemeMode(mode = ThemeMode.LIGHT))
            runCurrent()
            assertEquals(listOf(AppThemeMode.LIGHT), theme.writes)
            assertEquals(ThemeMode.DARK, model.container.stateFlow.value.themeMode)
            theme.themeMode.value = AppThemeMode.LIGHT
            runCurrent()
            assertEquals(ThemeMode.LIGHT, model.container.stateFlow.value.themeMode)
        }
    }

    @Test
    fun `failed palette write retains prior mode and reports localized failure effect`() = runTest {
        withModel { model, _, theme, _ ->
            theme.writeFailure = IOException("storage unavailable")
            model.handleAction(Action.SetThemeMode(mode = ThemeMode.LIGHT))
            runCurrent()
            assertEquals(ThemeMode.DARK, model.container.stateFlow.value.themeMode)
            assertEquals(Effect.ShowMessage(messageResId = R.string.more_theme_save_failed), model.container.sideEffectFlow.first())
        }
    }

    @Test
    fun `emergency close waits for explicit confirmation and clears progress after completion`() = runTest {
        withModel { model, _, _, emergency ->
            model.handleAction(Action.ShowEmergencyCloseDialog)
            runCurrent()
            assertTrue(model.container.stateFlow.value.isEmergencyCloseDialogVisible)
            assertEquals(0, emergency.calls)
            model.handleAction(Action.DismissEmergencyCloseDialog)
            runCurrent()
            assertFalse(model.container.stateFlow.value.isEmergencyCloseDialogVisible)
            assertEquals(0, emergency.calls)
            val gate = CompletableDeferred<Unit>()
            emergency.gate = gate
            model.handleAction(Action.ShowEmergencyCloseDialog)
            model.handleAction(Action.ConfirmEmergencyClose)
            runCurrent()
            assertEquals(1, emergency.calls)
            assertTrue(model.container.stateFlow.value.isEmergencyCloseInProgress)
            assertFalse(model.container.stateFlow.value.isEmergencyCloseDialogVisible)
            gate.complete(Unit)
            runCurrent()
            assertFalse(model.container.stateFlow.value.isEmergencyCloseInProgress)
            assertEquals(Effect.ShowMessage(messageResId = R.string.emergency_close_started), model.container.sideEffectFlow.first())
        }
    }

    @Test
    fun `emergency command cannot run without the confirmation dialog`() = runTest {
        withModel { model, _, _, emergency ->
            model.handleAction(Action.ConfirmEmergencyClose)
            runCurrent()

            assertEquals(0, emergency.calls)
            assertFalse(model.container.stateFlow.value.isEmergencyCloseInProgress)
        }
    }

    @Test
    fun `repeated confirmation cannot submit a second pending emergency command`() = runTest {
        withModel { model, _, _, emergency ->
            val gate = CompletableDeferred<Unit>()
            emergency.gate = gate
            model.handleAction(Action.ShowEmergencyCloseDialog)
            model.handleAction(Action.ConfirmEmergencyClose)
            runCurrent()
            model.handleAction(Action.ConfirmEmergencyClose)
            runCurrent()

            assertEquals(1, emergency.calls)
            assertTrue(model.container.stateFlow.value.isEmergencyCloseInProgress)
            gate.complete(Unit)
            runCurrent()
            assertEquals(1, emergency.calls)
            assertEquals(Effect.ShowMessage(messageResId = R.string.emergency_close_started), model.container.sideEffectFlow.first())
        }
    }

    @Test
    fun `failed emergency request reports failure and leaves controls usable`() = runTest {
        withModel { model, _, _, emergency ->
            emergency.failure = IOException("request unavailable")
            model.handleAction(Action.ShowEmergencyCloseDialog)
            model.handleAction(Action.ConfirmEmergencyClose)
            runCurrent()
            assertFalse(model.container.stateFlow.value.isEmergencyCloseInProgress)
            assertFalse(model.container.stateFlow.value.isEmergencyCloseDialogVisible)
            assertEquals(Effect.ShowMessage(messageResId = R.string.emergency_close_failed), model.container.sideEffectFlow.first())
        }
    }

    @Test
    fun `navigation actions emit their destinations and logout invokes auth`() = runTest {
        withModel { model, auth, _, _ ->
            val destinations = listOf(
                "instruments" to Effect.NavigateToInstruments,
                "history" to Effect.NavigateToHistory,
                "settings" to Effect.NavigateToSettings,
                "about" to Effect.NavigateToAbout
            )
            destinations.forEach { (id, expected) ->
                model.handleAction(Action.NavigateTo(itemId = id))
                runCurrent()
                assertEquals(expected, model.container.sideEffectFlow.first())
            }
            model.handleAction(Action.Logout)
            runCurrent()
            assertEquals(1, auth.logouts)
            assertEquals(AuthSession.SignedOut, auth.session.value)
        }
    }

    @Test
    fun `notification action updates preference and emits feedback`() = runTest {
        withModel { model, _, _, _ ->
            model.handleAction(Action.SetNotificationsEnabled(enabled = false))
            runCurrent()
            assertFalse(model.container.stateFlow.value.notificationsEnabled)
            assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        }
    }

    private suspend fun TestScope.withModel(
        block: suspend TestScope.(MoreViewModel, FakeAuth, FakeTheme, FakeEmergency) -> Unit
    ) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val auth = FakeAuth()
            val theme = FakeTheme()
            val appInfo = FakeAppInfoInteractor()
            val emergency = FakeEmergency()
            val model = MoreViewModel(
                analyticManager = mock(AnalyticManager::class.java),
                auth = auth,
                themeInteractor = theme,
                emergencyCloseInteractor = emergency,
                appInfoInteractor = appInfo
            )
            store.put("more", model)
            controlOrbit(model)
            block(model, auth, theme, emergency)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    private class FakeAuth : AuthInteractor {
        override val session = MutableStateFlow<AuthSession>(AuthSession.SignedIn(email = "owner@example.test", sessionId = "session"))
        var logouts = 0
        override suspend fun restore() = Unit
        override suspend fun login(email: String, password: String) = Unit
        override suspend fun logout() {
            logouts++
            session.value = AuthSession.SignedOut
        }
    }

    private class FakeTheme : ThemeInteractor {
        override val themeMode = MutableStateFlow(AppThemeMode.DARK)
        val writes = mutableListOf<AppThemeMode>()
        var writeFailure: Exception? = null
        var publishWrites = true
        override suspend fun setThemeMode(mode: AppThemeMode) {
            writes += mode
            writeFailure?.let { throw it }
            if (publishWrites) themeMode.value = mode
        }
    }

    private class FakeAppInfoInteractor: AppInfoInteractor {
        override fun getAppVersion(): String = "1.0.0"

        override fun getBuildNumber(): String = "1"

    }

    private class FakeEmergency : EmergencyCloseInteractor {
        var calls = 0
        var failure: Exception? = null
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun closeAllPositions() {
            calls++
            failure?.let { throw it }
            gate?.await()
        }
    }
}
