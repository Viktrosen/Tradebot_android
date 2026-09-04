package ru.bolotov.features.more.presentation.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для ThemeMode enum.
 *
 * Проверяют:
 * - все режимы темы существуют
 * - корректность значений
 */
class ThemeModeTest {

    @Test
    fun `All 3 theme modes must exist`() {
        val modes = ThemeMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(ThemeMode.LIGHT))
        assertTrue(modes.contains(ThemeMode.DARK))
        assertTrue(modes.contains(ThemeMode.SYSTEM))
    }

    @Test
    fun `ThemeMode values must be distinct`() {
        val modes = ThemeMode.values()
        assertEquals(modes.size, modes.distinct().size)
    }

    @Test
    fun `ThemeMode must support valueOf`() {
        assertEquals(ThemeMode.LIGHT, ThemeMode.valueOf("LIGHT"))
        assertEquals(ThemeMode.DARK, ThemeMode.valueOf("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.valueOf("SYSTEM"))
    }

    @Test
    fun `ThemeMode must support name property`() {
        assertEquals("LIGHT", ThemeMode.LIGHT.name)
        assertEquals("DARK", ThemeMode.DARK.name)
        assertEquals("SYSTEM", ThemeMode.SYSTEM.name)
    }
}
