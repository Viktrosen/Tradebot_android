package ru.bolotov.feature.positions.presentation.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для PositionFilter enum.
 *
 * Проверяют:
 * - все фильтры существуют
 * - корректность значений
 */
class PositionFilterTest {

    @Test
    fun `All 4 position filters must exist`() {
        val filters = PositionFilter.values()
        assertEquals(4, filters.size)
        assertTrue(filters.contains(PositionFilter.ALL))
        assertTrue(filters.contains(PositionFilter.OPEN))
        assertTrue(filters.contains(PositionFilter.PROFIT))
        assertTrue(filters.contains(PositionFilter.LOSS))
    }

    @Test
    fun `PositionFilter values must be distinct`() {
        val filters = PositionFilter.values()
        assertEquals(filters.size, filters.distinct().size)
    }

    @Test
    fun `PositionFilter must support valueOf`() {
        assertEquals(PositionFilter.ALL, PositionFilter.valueOf("ALL"))
        assertEquals(PositionFilter.OPEN, PositionFilter.valueOf("OPEN"))
        assertEquals(PositionFilter.PROFIT, PositionFilter.valueOf("PROFIT"))
        assertEquals(PositionFilter.LOSS, PositionFilter.valueOf("LOSS"))
    }
}
