package ru.bolotov.feature.instruments.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для TradingInstrument.
 *
 * Проверяют:
 * - корректность создания инструмента
 * - статус доступности торговли
 * - equality и hashCode
 */
class TradingInstrumentTest {

    @Test
    fun `TradingInstrument must have all required fields`() {
        val instrument = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        assertEquals("uid-1", instrument.id)
        assertEquals("SBER", instrument.ticker)
        assertEquals("Сбербанк", instrument.name)
        assertTrue(instrument.tradingAvailable)
    }

    @Test
    fun `TradingInstrument must support unavailable trading`() {
        val instrument = TradingInstrument(
            id = "uid-2",
            ticker = "GAZP",
            name = "Газпром",
            tradingAvailable = false
        )

        assertFalse(instrument.tradingAvailable)
    }

    @Test
    fun `TradingInstrument must be equal when all fields match`() {
        val instrument1 = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        val instrument2 = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        assertEquals(instrument1, instrument2)
    }

    @Test
    fun `TradingInstrument must have correct hashCode`() {
        val instrument = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        val instrumentCopy = instrument.copy()
        assertEquals(instrument.hashCode(), instrumentCopy.hashCode())
    }

    @Test
    fun `TradingInstrument must support copy with modified fields`() {
        val instrument = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        val unavailableInstrument = instrument.copy(tradingAvailable = false)

        assertFalse(unavailableInstrument.tradingAvailable)
        assertEquals("SBER", unavailableInstrument.ticker)
    }

    @Test
    fun `TradingInstrument names must be non-empty`() {
        val instrument = TradingInstrument(
            id = "uid-1",
            ticker = "SBER",
            name = "Сбербанк",
            tradingAvailable = true
        )

        assertTrue(instrument.name.isNotBlank())
        assertTrue(instrument.ticker.isNotBlank())
        assertTrue(instrument.id.isNotBlank())
    }
}
