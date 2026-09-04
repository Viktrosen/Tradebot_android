package ru.bolotov.feature.instruments.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для InstrumentFilters.
 *
 * Проверяют:
 * - корректность создания фильтров
 * - значения по умолчанию
 * - валидацию параметров
 */
class InstrumentFiltersTest {

    @Test
    fun `InstrumentFilters must have all required fields`() {
        val filters = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        assertEquals(1000000L, filters.minDailyVolume)
        assertEquals(0.5, filters.minVolatility, 0.001)
        assertEquals(5.0, filters.maxVolatility, 0.001)
        assertEquals(50, filters.maxCount)
    }

    @Test
    fun `InstrumentFilters must support zero values`() {
        val filters = InstrumentFilters(
            minDailyVolume = 0L,
            minVolatility = 0.0,
            maxVolatility = 0.0,
            maxCount = 0
        )

        assertEquals(0L, filters.minDailyVolume)
        assertEquals(0.0, filters.minVolatility, 0.001)
        assertEquals(0, filters.maxCount)
    }

    @Test
    fun `InstrumentFilters must support equality`() {
        val filters1 = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        val filters2 = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        assertEquals(filters1, filters2)
    }

    @Test
    fun `InstrumentFilters must have correct hashCode`() {
        val filters = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        val filtersCopy = filters.copy()
        assertEquals(filters.hashCode(), filtersCopy.hashCode())
    }

    @Test
    fun `InstrumentFilters must support copy with modified fields`() {
        val filters = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        val updatedFilters = filters.copy(
            minDailyVolume = 2000000L,
            maxCount = 100
        )

        assertEquals(2000000L, updatedFilters.minDailyVolume)
        assertEquals(100, updatedFilters.maxCount)
        assertEquals(0.5, updatedFilters.minVolatility, 0.001)
    }

    @Test
    fun `InstrumentFilters maxVolatility must be greater than minVolatility`() {
        val validFilters = InstrumentFilters(
            minDailyVolume = 1000000L,
            minVolatility = 0.5,
            maxVolatility = 5.0,
            maxCount = 50
        )

        assertTrue("maxVolatility must be > minVolatility", validFilters.maxVolatility > validFilters.minVolatility)
    }
}
