package ru.bolotov.feature.strategy.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class VotingWeightsTest {
    @Test
    fun `saving all configured participants retains SuperTrend and VWAP`() {
        val configured = mapOf("EMA" to 3, "RSI" to 2, "MACD" to 2, "BB" to 2, "SUPER_TREND" to 10, "VWAP" to 7)

        assertEquals(configured, VotingWeights.from(configured).toPayload())
    }

    @Test
    fun `saving a subset does not enable missing participants`() {
        val configured = mapOf("SUPER_TREND" to 10, "VWAP" to 1)

        assertEquals(configured, VotingWeights.from(configured).toPayload())
    }

    @Test
    fun `enabled weights accept endpoints and reject values outside one through ten`() {
        val draft = VotingWeights.from(mapOf("EMA" to 1, "VWAP" to 10))

        assertTrue(draft.isValid)
        assertEquals(10, draft.withWeight(participant = "EMA", weight = 10).toPayload()["EMA"])
        assertThrows(IllegalArgumentException::class.java) { draft.withWeight(participant = "EMA", weight = 0) }
        assertThrows(IllegalArgumentException::class.java) { draft.withWeight(participant = "EMA", weight = 11) }
    }

    @Test
    fun `disabling removes a participant instead of posting zero weight`() {
        val draft = VotingWeights.from(mapOf("EMA" to 3, "RSI" to 2, "VWAP" to 9))
            .withParticipant(participant = "RSI", enabled = false)

        assertEquals(mapOf("EMA" to 3, "VWAP" to 9), draft.toPayload())
        assertEquals(12, draft.totalWeight)
    }

    @Test
    fun `enabling adds only the selected participant with a valid initial weight`() {
        val draft = VotingWeights.from(mapOf("SUPER_TREND" to 7, "VWAP" to 8))
            .withParticipant(participant = "EMA", enabled = true)

        assertEquals(mapOf("SUPER_TREND" to 7, "VWAP" to 8, "EMA" to 1), draft.toPayload())
    }

    @Test
    fun `fewer than two participants cannot be sent`() {
        val draft = VotingWeights.from(mapOf("EMA" to 3))

        assertEquals(VotingWeightsError.TOO_FEW_PARTICIPANTS, draft.validationError)
        assertThrows(IllegalStateException::class.java) { draft.toPayload() }
    }

    @Test
    fun `unsupported server participants remain visible and block destructive save`() {
        val source = mapOf("EMA" to 3, "VWAP" to 8, "FUTURE_INDICATOR" to 4)
        val draft = VotingWeights.from(source).withWeight(participant = "EMA", weight = 5)

        assertEquals(4, draft.values["FUTURE_INDICATOR"])
        assertEquals(setOf("FUTURE_INDICATOR"), draft.unsupportedParticipants)
        assertEquals(VotingWeightsError.UNSUPPORTED_PARTICIPANTS, draft.validationError)
        assertThrows(IllegalStateException::class.java) { draft.toPayload() }
    }

    @Test
    fun `invalid loaded weights are not silently clamped or posted`() {
        val draft = VotingWeights.from(mapOf("EMA" to 0, "RSI" to 11))

        assertFalse(draft.isValid)
        assertEquals(VotingWeightsError.INVALID_WEIGHT, draft.validationError)
        assertEquals(0, draft.values["EMA"])
        assertThrows(IllegalStateException::class.java) { draft.toPayload() }
    }
}
