package ru.bolotov.tradebot.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.tradebot.navigation.data.*

@OptIn(ExperimentalCoroutinesApi::class)
class AppLinkStateTest {
    private class Auth : AuthInteractor {
        override val session = MutableStateFlow<AuthSession>(AuthSession.SignedOut)

        override suspend fun restore() = Unit

        override suspend fun login(email: String, password: String) = Unit

        override suspend fun logout() {
            session.value = AuthSession.SignedOut
        }
    }

    private val themes =
        object : ThemeInteractor {
            override val themeMode = MutableStateFlow(AppThemeMode.SYSTEM)

            override suspend fun setThemeMode(mode: AppThemeMode) {
                themeMode.value = mode
            }
        }
    private val destination =
        AppLink.PositionDetails(positionId = "31fcf18a-350d-443d-9d69-871d4c25a4d9")

    @Test
    fun `pending target survives host restoration but is not exposed before authorization`() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val store = ViewModelStore()
            try {
                val auth = Auth()
                val saved = SavedStateHandle()
                val first =
                    AppViewModel(
                        auth = auth,
                        themeInteractor = themes,
                        savedStateHandle = saved,
                        json = Json,
                    )
                store.put("first", first)
                first.acceptLink(destination = destination)
                assertEquals(AppNavigationState.Auth, first.navigationState.first())
                val restored = SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) })
                val second =
                    AppViewModel(
                        auth = auth,
                        themeInteractor = themes,
                        savedStateHandle = restored,
                        json = Json,
                    )
                store.put("second", second)
                auth.session.value = AuthSession.SignedIn("owner@example.com", "session")
                val main = second.navigationState.first() as AppNavigationState.Main
                val pending = requireNotNull(main.pendingLink)
                assertEquals(destination, pending.destination)
                assertEquals(destination.positionId, pending.toMainRoute().initialPositionId)
                assertTrue(
                    main.requiresNavigation(
                        isAuthDestination = false,
                        isMainDestination = true,
                        displayedSessionId = "session",
                    )
                )
                second.acknowledgeLink(requestId = pending.requestId)
                val consumed = second.navigationState.first() as AppNavigationState.Main
                assertNull(consumed.pendingLink)
                assertFalse(
                    consumed.requiresNavigation(
                        isAuthDestination = false,
                        isMainDestination = true,
                        displayedSessionId = "session",
                    )
                )
            } finally {
                store.clear()
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `older acknowledgement cannot discard a new tap on the same position`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val auth =
                Auth().apply {
                    session.value = AuthSession.SignedIn("owner@example.com", "session")
                }
            val model =
                AppViewModel(
                    auth = auth,
                    themeInteractor = themes,
                    savedStateHandle = SavedStateHandle(),
                    json = Json,
                )
            store.put("model", model)
            model.acceptLink(destination = destination)
            val first =
                requireNotNull(
                    (model.navigationState.first() as AppNavigationState.Main).pendingLink
                )
            model.acceptLink(destination = destination)
            val second =
                requireNotNull(
                    (model.navigationState.first() as AppNavigationState.Main).pendingLink
                )
            assertNotEquals(first.requestId, second.requestId)
            model.acknowledgeLink(requestId = first.requestId)
            assertEquals(
                second,
                (model.navigationState.first() as AppNavigationState.Main).pendingLink,
            )
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
