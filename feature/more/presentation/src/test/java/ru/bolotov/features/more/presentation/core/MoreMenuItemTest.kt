package ru.bolotov.features.more.presentation.core

import org.junit.Assert.*
import org.junit.Test

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
        val items = listOf(
            MoreMenuItem.Instruments,
            MoreMenuItem.History,
            MoreMenuItem.Settings,
            MoreMenuItem.About,
            MoreMenuItem.Logout
        )

        assertEquals(5, items.size)
    }

    @Test
    fun `MoreMenuItem.Instruments must have correct properties`() {
        val item = MoreMenuItem.Instruments

        assertEquals("instruments", item.id)
        assertEquals("Инструменты", item.title)
        assertNull(item.badge)
    }

    @Test
    fun `MoreMenuItem.History must have correct properties`() {
        val item = MoreMenuItem.History

        assertEquals("history", item.id)
        assertEquals("История сделок", item.title)
        assertNull(item.badge)
    }

    @Test
    fun `MoreMenuItem.Settings must have correct properties`() {
        val item = MoreMenuItem.Settings

        assertEquals("settings", item.id)
        assertEquals("Настройки", item.title)
        assertNull(item.badge)
    }

    @Test
    fun `MoreMenuItem.About must have correct properties`() {
        val item = MoreMenuItem.About

        assertEquals("about", item.id)
        assertEquals("О приложении", item.title)
        assertNull(item.badge)
    }

    @Test
    fun `MoreMenuItem.Logout must have correct properties`() {
        val item = MoreMenuItem.Logout

        assertEquals("logout", item.id)
        assertEquals("Выход", item.title)
        assertNull(item.badge)
    }

    @Test
    fun `All menu items must implement MoreMenuItem`() {
        val items: List<MoreMenuItem> = listOf(
            MoreMenuItem.Instruments,
            MoreMenuItem.History,
            MoreMenuItem.Settings,
            MoreMenuItem.About,
            MoreMenuItem.Logout
        )

        items.forEach { item ->
            assertTrue(item is MoreMenuItem)
        }
    }

    @Test
    fun `All menu item IDs must be unique`() {
        val items = listOf(
            MoreMenuItem.Instruments,
            MoreMenuItem.History,
            MoreMenuItem.Settings,
            MoreMenuItem.About,
            MoreMenuItem.Logout
        )

        val ids = items.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `MoreMenuItem must support when expression with exhaustive check`() {
        val items = listOf(
            MoreMenuItem.Instruments,
            MoreMenuItem.History,
            MoreMenuItem.Settings,
            MoreMenuItem.About,
            MoreMenuItem.Logout
        )

        val ids = items.map { item ->
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
    fun `MoreMenuItem must support copy with badge`() {
        val item = MoreMenuItem.Settings.copy(badge = "New")

        assertEquals("settings", item.id)
        assertEquals("Настройки", item.title)
        assertEquals("New", item.badge)
    }
}
