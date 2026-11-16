package ru.bolotov.feature.positionDetails.presentation.ui

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PositionDetailsFormattersTest {
    private val russian = Locale.forLanguageTag("ru")
    private val clock =
        Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneId.of("Asia/Yekaterinburg"))

    @Test
    fun `current year is omitted and local time has no zone suffix`() {
        assertEquals(
            "05 ноября 12:03",
            formatPositionDateTime(value = "2026-11-05T07:03:00Z", locale = russian, clock = clock),
        )
    }

    @Test
    fun `past and future years are displayed`() {
        assertEquals(
            "05 ноября 2025 12:03",
            formatPositionDateTime(value = "2025-11-05T07:03:00Z", locale = russian, clock = clock),
        )
        assertEquals(
            "05 ноября 2027 12:03",
            formatPositionDateTime(value = "2027-11-05T07:03:00Z", locale = russian, clock = clock),
        )
    }

    @Test
    fun `full month follows the selected locale`() {
        assertEquals(
            "05 November 12:03",
            formatPositionDateTime(
                value = "2026-11-05T07:03:00Z",
                locale = Locale.ENGLISH,
                clock = clock,
            ),
        )
    }

    @Test
    fun `year comparison uses local date on both sides of the boundary`() {
        val newYearClock =
            Clock.fixed(Instant.parse("2025-12-31T20:00:00Z"), ZoneId.of("Asia/Yekaterinburg"))
        assertEquals(
            "01 января 00:30",
            formatPositionDateTime(
                value = "2025-12-31T19:30:00Z",
                locale = russian,
                clock = newYearClock,
            ),
        )
        assertEquals(
            "31 декабря 2025 23:30",
            formatPositionDateTime(
                value = "2025-12-31T18:30:00Z",
                locale = russian,
                clock = newYearClock,
            ),
        )
    }

    @Test
    fun `missing and invalid dates remain unavailable`() {
        assertNull(formatPositionDateTime(value = null, locale = russian, clock = clock))
        assertNull(formatPositionDateTime(value = "bad-date", locale = russian, clock = clock))
    }
}
