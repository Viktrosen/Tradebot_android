package ru.bolotov.tradebot.navigation

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.tradebot.navigation.data.AppNavigationState

@OptIn(ExperimentalCoroutinesApi::class)
class AppThemeStateTest {
    @Test
    fun `startup waits for preference and palette changes do not reset session`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val themes =
                object : ThemeInteractor {
                    override val themeMode = MutableSharedFlow<AppThemeMode>(replay = 1)

                    override suspend fun setThemeMode(mode: AppThemeMode) {
                        themeMode.emit(mode)
                    }
                }
            val auth =
                object : AuthInteractor {
                    override val session = MutableStateFlow<AuthSession>(AuthSession.Checking)
                    var restores = 0

                    override suspend fun restore() {
                        restores++
                        session.value = AuthSession.SignedIn("owner@example.com", "session")
                    }

                    override suspend fun login(email: String, password: String) = Unit

                    override suspend fun logout() {
                        session.value = AuthSession.SignedOut
                    }
                }
            val viewModel = AppViewModel(auth, themes)
            store.put("host", viewModel)
            val navigationInstance = viewModel.navigationInstanceId
            runCurrent()
            assertNull(viewModel.themeMode.value)
            for (mode in AppThemeMode.entries) {
                themes.setThemeMode(mode)
                runCurrent()
                assertEquals(mode, viewModel.themeMode.value)
                assertEquals(navigationInstance, viewModel.navigationInstanceId)
                assertEquals(AppNavigationState.Main("session"), viewModel.navigationState.first())
            }
            assertEquals(1, auth.restores)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
