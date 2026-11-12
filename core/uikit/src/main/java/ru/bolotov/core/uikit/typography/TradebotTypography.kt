package ru.bolotov.core.uikit.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.bolotov.tradebot.core.uikit.R

private val Inter = FontFamily(Font(R.font.inter))

/** One type scale, including Cyrillic, for all feature modules. */
@Immutable
class TradebotTypography {
    val screenTitle = style(28, 34, FontWeight.Bold)
    val amount = style(28, 36, FontWeight.SemiBold).copy(fontFeatureSettings = "tnum")
    val section = style(11, 16, FontWeight.Medium).copy(letterSpacing = 2.sp)
    val label = style(11, 16, FontWeight.Medium)
    val material =
        Typography(
            headlineLarge = screenTitle,
            headlineMedium = style(24, 30, FontWeight.Bold),
            headlineSmall = style(22, 28, FontWeight.SemiBold),
            titleLarge = style(20, 26, FontWeight.SemiBold),
            titleMedium = style(16, 22, FontWeight.SemiBold),
            titleSmall = style(14, 20, FontWeight.SemiBold),
            bodyLarge = style(16, 24),
            bodyMedium = style(14, 20),
            bodySmall = style(12, 18),
            labelLarge = style(14, 20, FontWeight.SemiBold),
            labelMedium = style(12, 16, FontWeight.Medium),
            labelSmall = label,
        )
}

private fun style(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = Inter, fontSize = size.sp, lineHeight = height.sp, fontWeight = weight)
