package ru.bolotov.tradebot.navigation

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.tradebot.navigation.data.AppNavigationState
import ru.bolotov.tradebot.navigation.data.requiresNavigation
import ru.bolotov.tradebot.navigation.data.toAppNavigationState

class AppNavigationStateTest {
    @Test fun `unchecked offline and signed out sessions never select Main`() {
        listOf(AuthSession.Checking, AuthSession.Unavailable, AuthSession.SignedOut).forEach {
            assertEquals(AppNavigationState.Auth, it.toAppNavigationState())
        }
    }

    @Test fun `token rotation in the same session does not reset navigation`() {
        val before = AuthSession.SignedIn("owner@example.com", "session-1").toAppNavigationState()
        val after = AuthSession.SignedIn("owner@example.com", "session-1").toAppNavigationState()
        assertEquals(before, after)
        assertFalse(after.requiresNavigation(false, true, "session-1"))
    }

    @Test fun `new login replaces Main even when logout happened in background`() {
        val state = AppNavigationState.Main("session-2")
        assertTrue(state.requiresNavigation(false, true, "session-1"))
    }

    @Test fun `logout clears Main and re-collection on Auth is idempotent`() {
        assertTrue(AppNavigationState.Auth.requiresNavigation(false, true, "session-1"))
        assertFalse(AppNavigationState.Auth.requiresNavigation(true, false, null))
    }

    @Test fun `successful restore moves Auth to Main`() {
        assertTrue(AppNavigationState.Main("session-1").requiresNavigation(true, false, null))
    }
}
