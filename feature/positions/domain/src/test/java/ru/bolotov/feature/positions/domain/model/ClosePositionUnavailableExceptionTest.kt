package ru.bolotov.feature.positions.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit-тесты для ClosePositionUnavailableException.
 *
 * Проверяют:
 * - исключение является подклассом IllegalStateException
 * - корректное поведение при создании
 */
class ClosePositionUnavailableExceptionTest {

    @Test
    fun `ClosePositionUnavailableException must extend IllegalStateException`() {
        val exception = ClosePositionUnavailableException()
        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `ClosePositionUnavailableException must have no message by default`() {
        val exception = ClosePositionUnavailableException()
        assertNull(exception.message)
    }

    @Test
    fun `ClosePositionUnavailableException must support custom message`() {
        val message = "Position is closed"
        val exception = ClosePositionUnavailableException(message)
        assertEquals(message, exception.message)
    }

    @Test
    fun `ClosePositionUnavailableException must be throwable`() {
        assertThrows(ClosePositionUnavailableException::class.java) {
            throw ClosePositionUnavailableException("Cannot close")
        }
    }

    @Test
    fun `Multiple instances must be equal when created without message`() {
        val exception1 = ClosePositionUnavailableException()
        val exception2 = ClosePositionUnavailableException()
        assertEquals(exception1.javaClass, exception2.javaClass)
    }
}
