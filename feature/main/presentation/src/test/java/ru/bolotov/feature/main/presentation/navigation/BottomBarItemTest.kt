package ru.bolotov.feature.main.presentation.navigation

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для BottomBarItem и BottomBarTag.
 *
 * Проверяют:
 * - все теги навигации
 * - корректность создания элементов навигации
 * - equality и hashCode
 */
class BottomBarTagTest {

    @Test
    fun `All 5 navigation tags must exist`() {
        val tags = BottomBarTag.values()
        assertEquals(5, tags.size)
        assertTrue(tags.contains(BottomBarTag.Dashboard))
        assertTrue(tags.contains(BottomBarTag.Positions))
        assertTrue(tags.contains(BottomBarTag.Strategy))
        assertTrue(tags.contains(BottomBarTag.Risk))
        assertTrue(tags.contains(BottomBarTag.More))
    }

    @Test
    fun `BottomBarTag values must be distinct`() {
        val tags = BottomBarTag.values()
        assertEquals(tags.size, tags.distinct().size)
    }
}

class BottomBarItemTest {

    @Test
    fun `BottomBarItem must have all required fields`() {
        val item = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard
        )

        assertEquals("dashboard", item.route)
        assertEquals(1, item.icon)
        assertEquals(2, item.text)
        assertEquals(BottomBarTag.Dashboard, item.tag)
    }

    @Test
    fun `BottomBarItem must support graphRoute`() {
        val itemWithGraph = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard,
            graphRoute = "dashboard_graph"
        )

        assertEquals("dashboard_graph", itemWithGraph.graphRoute)
    }

    @Test
    fun `BottomBarItem must support null graphRoute`() {
        val item = BottomBarItem(
            route = "positions",
            icon = 2,
            text = 3,
            tag = BottomBarTag.Positions
        )

        assertNull(item.graphRoute)
    }

    @Test
    fun `BottomBarItem must be equal when all fields match`() {
        val item1 = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard
        )

        val item2 = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard
        )

        assertEquals(item1, item2)
    }

    @Test
    fun `BottomBarItem must support copy with modified fields`() {
        val item = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard
        )

        val updated = item.copy(
            route = "dashboard_v2",
            graphRoute = "new_graph"
        )

        assertEquals("dashboard_v2", updated.route)
        assertEquals("new_graph", updated.graphRoute)
        assertEquals(1, updated.icon)
        assertEquals(2, updated.text)
        assertEquals(BottomBarTag.Dashboard, updated.tag)
    }

    @Test
    fun `BottomBarItem must have correct hashCode`() {
        val item = BottomBarItem(
            route = "dashboard",
            icon = 1,
            text = 2,
            tag = BottomBarTag.Dashboard
        )

        val itemCopy = item.copy()
        assertEquals(item.hashCode(), itemCopy.hashCode())
    }

    @Test
    fun `All navigation routes must be unique`() {
        val items = listOf(
            BottomBarItem("dashboard", 1, 2, BottomBarTag.Dashboard),
            BottomBarItem("positions", 2, 3, BottomBarTag.Positions),
            BottomBarItem("strategy", 3, 4, BottomBarTag.Strategy),
            BottomBarItem("risk", 4, 5, BottomBarTag.Risk),
            BottomBarItem("more", 5, 6, BottomBarTag.More)
        )

        val routes = items.map { it.route }
        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `BottomBarItem must distinguish different tags`() {
        val items = listOf(
            BottomBarItem("dashboard", 1, 2, BottomBarTag.Dashboard),
            BottomBarItem("positions", 2, 3, BottomBarTag.Positions),
            BottomBarItem("strategy", 3, 4, BottomBarTag.Strategy),
            BottomBarItem("risk", 4, 5, BottomBarTag.Risk),
            BottomBarItem("more", 5, 6, BottomBarTag.More)
        )

        val tags = items.map { it.tag }
        assertEquals(tags.size, tags.distinct().size)
    }
}
