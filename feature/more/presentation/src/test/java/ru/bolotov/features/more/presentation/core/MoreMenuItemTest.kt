package ru.bolotov.features.more.presentation.core

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.tradebot.feature.more.presentation.R

/**
 * Unit-тесты для MoreMenuItem sealed class.
 *
 * Проверяют:
 * - все пункты меню существуют
 * - корректность id и title
 * - sealed class иерархию
 */
class MoreMenuItemTest {

    @Test
    fun `All 5 menu items must exist`() {
        val items =
            listOf(
                MoreMenuItem.Instruments,
                MoreMenuItem.History,
                MoreMenuItem.Settings,
                MoreMenuItem.About,
                MoreMenuItem.Logout,
            )

        assertEquals(5, items.size)
    }

    @Test
    fun `Instruments menu item must have correct properties`() {
        val item = MoreMenuItem.Instruments

        assertEquals("instruments", item.id)
        assertEquals(R.string.more_instruments, item.titleRes)
        assertNull(item.badge)
    }

    @Test
    fun `History menu item must have correct properties`() {
        val item = MoreMenuItem.History

        assertEquals("history", item.id)
        assertEquals(R.string.more_history, item.titleRes)
        assertNull(item.badge)
    }

    @Test
    fun `Settings menu item must have correct properties`() {
        val item = MoreMenuItem.Settings

        assertEquals("settings", item.id)
        assertEquals(R.string.more_settings, item.titleRes)
        assertNull(item.badge)
    }

    @Test
    fun `About menu item must have correct properties`() {
        val item = MoreMenuItem.About

        assertEquals("about", item.id)
        assertEquals(R.string.more_about, item.titleRes)
        assertNull(item.badge)
    }

    @Test
    fun `Logout menu item must have correct properties`() {
        val item = MoreMenuItem.Logout

        assertEquals("logout", item.id)
        assertEquals(R.string.more_logout, item.titleRes)
        assertNull(item.badge)
    }

    @Test
    fun `All menu items must implement MoreMenuItem`() {
        val items: List<MoreMenuItem> =
            listOf(
                MoreMenuItem.Instruments,
                MoreMenuItem.History,
                MoreMenuItem.Settings,
                MoreMenuItem.About,
                MoreMenuItem.Logout,
            )

        items.forEach { item -> assertTrue(item is MoreMenuItem) }
    }

    @Test
    fun `All menu item IDs must be unique`() {
        val items =
            listOf(
                MoreMenuItem.Instruments,
                MoreMenuItem.History,
                MoreMenuItem.Settings,
                MoreMenuItem.About,
                MoreMenuItem.Logout,
            )

        val ids = items.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `MoreMenuItem must support when expression with exhaustive check`() {
        val items =
            listOf(
                MoreMenuItem.Instruments,
                MoreMenuItem.History,
                MoreMenuItem.Settings,
                MoreMenuItem.About,
                MoreMenuItem.Logout,
            )

        val ids =
            items.map { item ->
                when (item) {
                    is MoreMenuItem.Instruments -> "instruments"
                    is MoreMenuItem.History -> "history"
                    is MoreMenuItem.Settings -> "settings"
                    is MoreMenuItem.About -> "about"
                    is MoreMenuItem.Logout -> "logout"
                }
            }

        assertEquals(5, ids.size)
        assertTrue(ids.contains("instruments"))
        assertTrue(ids.contains("history"))
        assertTrue(ids.contains("settings"))
        assertTrue(ids.contains("about"))
        assertTrue(ids.contains("logout"))
    }

    @Test
    fun `Settings menu item is a singleton without mutable badge`() {
        assertSame(MoreMenuItem.Settings, MoreMenuItem.Settings)
        assertNull(MoreMenuItem.Settings.badge)
    }
}
