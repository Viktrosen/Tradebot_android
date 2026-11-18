package ru.bolotov.feature.dashboard.domain.core

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.repository.BotRepository

/** Tests production interactor sorting, not a copy of Kotlin's sorting expression. */
class DashboardInteractorSortingTest {
    @Test
    fun `newest known timestamps come first and unknown timestamps come last`() = runTest {
        val repository = RecordingRepository(listOf(
            position(name = "Unknown", entryTime = null),
            position(name = "Old", entryTime = "2026-09-01T08:00:00Z"),
            position(name = "New", entryTime = "2026-09-01T14:00:00Z")
        ))
        val sorted = DashboardInteractorImpl(repository).getPositions()
        assertEquals(listOf("New", "Old", "Unknown"), sorted.map(Position::instrumentName))
        assertEquals(1, repository.calls)
        assertEquals("Unknown", repository.positions.first().instrumentName)
    }

    @Test
    fun `empty and singleton results preserve their content`() = runTest {
        assertTrue(DashboardInteractorImpl(RecordingRepository(emptyList())).getPositions().isEmpty())
        val only = position(name = "Only", entryTime = null)
        assertEquals(listOf(only), DashboardInteractorImpl(RecordingRepository(listOf(only))).getPositions())
    }

    @Test
    fun `equal timestamps and unknown timestamps preserve relative order`() = runTest {
        val repository = RecordingRepository(listOf(
            position(name = "Unknown1", entryTime = null),
            position(name = "First", entryTime = "2026-09-01T10:00:00Z"),
            position(name = "Second", entryTime = "2026-09-01T10:00:00Z"),
            position(name = "Unknown2", entryTime = null)
        ))
        assertEquals(
            listOf("First", "Second", "Unknown1", "Unknown2"),
            DashboardInteractorImpl(repository).getPositions().map(Position::instrumentName)
        )
    }

    @Test
    fun `repository failure is not disguised as an empty portfolio`() = runTest {
        val repository = RecordingRepository(emptyList())
        repository.failure = IllegalStateException("Unavailable")
        val failure = runCatching { DashboardInteractorImpl(repository).getPositions() }.exceptionOrNull()
        assertEquals(repository.failure, failure)
    }

    private fun position(name: String, entryTime: String?) = Position(
        positionId = "position-$name", instrumentId = "instrument-$name",
        instrumentName = name, direction = "BUY", entryPrice = 100.0,
        quantity = 1, entryTime = entryTime,
        stopLossPrice = null, atr = null, pnl = null, currentPrice = null
    )

    private class RecordingRepository(val positions: List<Position>) : BotRepository {
        var calls = 0
        var failure: Exception? = null
        override suspend fun getPositions(): List<Position> {
            calls++
            failure?.let { throw it }
            return positions
        }
        override suspend fun getBotStatus(): BotStatus = error("Not requested")
        override suspend fun getDashboard(): Dashboard = error("Not requested")
        override suspend fun startBot() = error("Not requested")
        override suspend fun stopBot() = error("Not requested")
    }
}
