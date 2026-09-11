package ru.bolotov.feature.positions.presentation.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class AiExplanationFormatterTest {

    @Test
    fun `limits long decimal values in AI explanation to two fraction digits`() {
        val explanation = "Цена 4632.500000000, ATR 1,23456, уверенность 0.75"

        assertEquals(
            "Цена 4632.50, ATR 1,23, уверенность 0.75",
            explanation.formatAiExplanationNumbers()
        )
    }
}
