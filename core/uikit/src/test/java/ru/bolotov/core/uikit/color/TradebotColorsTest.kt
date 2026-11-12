package ru.bolotov.core.uikit.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TradebotColorsTest {
    @Test
    fun textAndActionsRemainReadableInBothPalettes() {
        listOf(DarkTradebotColors, LightTradebotColors).forEach { colors ->
            assertTrue(contrast(colors.text, colors.surface) >= 4.5f)
            assertTrue(contrast(colors.textSecondary, colors.surface) >= 4.5f)
            assertTrue(contrast(colors.onAccent, colors.accent) >= 4.5f)
        }
    }

    @Test
    fun financialSidesAndOutcomesHaveSeparateRoles() {
        listOf(DarkTradebotColors, LightTradebotColors).forEach { colors ->
            assertNotEquals(colors.profit, colors.loss)
            assertNotEquals(colors.profit, colors.accent)
            assertNotEquals(colors.short, colors.loss)
        }
    }

    private fun contrast(first: Color, second: Color): Float {
        val a = first.luminance()
        val b = second.luminance()
        return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
    }
}
