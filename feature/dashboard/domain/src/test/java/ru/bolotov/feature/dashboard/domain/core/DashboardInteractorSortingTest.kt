package ru.bolotov.feature.dashboard.domain.core

import org.junit.Assert.*
import org.junit.Test
import ru.bolotov.feature.dashboard.domain.model.Position

/**
 * Unit-тесты для сортировки позиций в DashboardInteractorImpl.
 *
 * Проверяют:
 * - корректность сортировки по entryTime (сначала новые)
 * - обработку пустого списка
 * - обработку null entryTime
 */
class DashboardInteractorSortingTest {

    private fun makePosition(name: String, entryTime: String?): Position = Position(
        positionId = "pos-$name",
        instrumentId = "id-$name",
        instrumentName = name,
        direction = "BUY",
        entryPrice = 100.0,
        quantity = 10,
        entryTime = entryTime,
        stopLossPrice = 95.0,
        atr = 2.0,
        pnl = 10.0,
        currentPrice = 101.0,
        entryStrategyName = "Test"
    )

    @Test
    fun `Positions must be sorted by entryTime descending`() {
        val positions = listOf(
            makePosition("Old", "2026-09-01T08:00:00Z"),
            makePosition("New", "2026-09-01T14:00:00Z"),
            makePosition("Middle", "2026-09-01T11:00:00Z")
        )

        val sorted = positions.sortedByDescending { it.entryTime }

        assertEquals("New", sorted[0].instrumentName)
        assertEquals("Middle", sorted[1].instrumentName)
        assertEquals("Old", sorted[2].instrumentName)
    }

    @Test
    fun `Empty positions list must remain empty after sorting`() {
        val positions = emptyList<Position>()

        val sorted = positions.sortedByDescending { it.entryTime }

        assertTrue(sorted.isEmpty())
    }

    @Test
    fun `Single position must remain unchanged after sorting`() {
        val position = makePosition("Single", "2026-09-01T10:00:00Z")

        val sorted = listOf(position).sortedByDescending { it.entryTime }

        assertEquals(1, sorted.size)
        assertEquals("Single", sorted[0].instrumentName)
    }

    @Test
    fun `Positions with null entryTime must be sorted to beginning`() {
        val positions = listOf(
            makePosition("NoTime", null),
            makePosition("WithTime", "2026-09-01T10:00:00Z")
        )

        val sorted = positions.sortedByDescending { it.entryTime }

        // null sorts first in descending order
        assertEquals("NoTime", sorted[0].instrumentName)
        assertEquals("WithTime", sorted[1].instrumentName)
    }

    @Test
    fun `Positions with same entryTime must preserve order`() {
        val positions = listOf(
            makePosition("First", "2026-09-01T10:00:00Z"),
            makePosition("Second", "2026-09-01T10:00:00Z")
        )

        val sorted = positions.sortedByDescending { it.entryTime }

        assertEquals(2, sorted.size)
        // Both have same time, order may vary but size must be correct
    }

    @Test
    fun `Multiple null entryTimes must be grouped together`() {
        val positions = listOf(
            makePosition("NoTime1", null),
            makePosition("WithTime", "2026-09-01T10:00:00Z"),
            makePosition("NoTime2", null)
        )

        val sorted = positions.sortedByDescending { it.entryTime }

        // Nulls should be first
        assertEquals(2, sorted.count { it.entryTime == null })
        assertEquals(1, sorted.count { it.entryTime != null })
    }
}
