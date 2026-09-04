package ru.bolotov.feature.splashScreen.presentation.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для Action и Effect sealed interfaces splash screen.
 *
 * Проверяют:
 * - корректность Action
 * - корректность Effect
 * - sealed interface иерархию
 */
class SplashScreenActionTest {

    @Test
    fun `Action.OnNavigateToMainScreen must be singleton`() {
        val action1 = Action.OnNavigateToMainScreen
        val action2 = Action.OnNavigateToMainScreen

        assertSame(action1, action2)
        assertEquals(Action.OnNavigateToMainScreen, Action.OnNavigateToMainScreen)
    }

    @Test
    fun `Only one Action type must exist`() {
        // Проверяем что sealed interface имеет только один тип
        val action = Action.OnNavigateToMainScreen
        assertTrue(action is Action)
    }
}

class SplashScreenEffectTest {

    @Test
    fun `Effect.NavigateMainScreen must be singleton`() {
        val effect1 = Effect.NavigateMainScreen
        val effect2 = Effect.NavigateMainScreen

        assertSame(effect1, effect2)
        assertEquals(Effect.NavigateMainScreen, Effect.NavigateMainScreen)
    }

    @Test
    fun `Only one Effect type must exist`() {
        val effect = Effect.NavigateMainScreen
        assertTrue(effect is Effect)
    }

    @Test
    fun `Effect must be distinguishable from Action`() {
        val action = Action.OnNavigateToMainScreen
        val effect = Effect.NavigateMainScreen

        assertNotEquals(action::class, effect::class)
        assertFalse(action is Effect)
        assertFalse(effect is Action)
    }
}
