package ru.bolotov.tradebot.theme

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.more.domain.model.AppThemeMode

class ResolveDarkThemeTest {
    @Test
    fun `explicit theme ignores system setting`() {
        for (systemDark in listOf(false, true)) {
            assertFalse(AppThemeMode.LIGHT.resolveDarkTheme(systemDark))
            assertTrue(AppThemeMode.DARK.resolveDarkTheme(systemDark))
        }
    }

    @Test
    fun `SYSTEM tracks both system configurations`() {
        assertFalse(AppThemeMode.SYSTEM.resolveDarkTheme(false))
        assertTrue(AppThemeMode.SYSTEM.resolveDarkTheme(true))
    }
}
