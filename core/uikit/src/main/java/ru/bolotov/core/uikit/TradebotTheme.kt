package ru.bolotov.core.uikit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.bolotov.core.uikit.color.DarkTradebotColors
import ru.bolotov.core.uikit.color.LightTradebotColors
import ru.bolotov.core.uikit.color.TradebotColors
import ru.bolotov.core.uikit.shapes.TradebotShapes
import ru.bolotov.core.uikit.typography.TradebotTypography

/** Brand tokens and Material 3 share the same roles, including dialogs and refresh indicators. */
@Composable
fun TradebotTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkTradebotColors else LightTradebotColors
    val scheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme =
                scheme.copy(
                    primary = colors.accent,
                    onPrimary = colors.onAccent,
                    primaryContainer = colors.surfaceRaised,
                    onPrimaryContainer = colors.text,
                    secondary = colors.accent,
                    onSecondary = colors.onAccent,
                    secondaryContainer = colors.surface,
                    onSecondaryContainer = colors.text,
                    tertiary = colors.brand,
                    onTertiary = colors.text,
                    background = colors.background,
                    onBackground = colors.text,
                    surface = colors.surface,
                    onSurface = colors.text,
                    surfaceVariant = colors.surfaceRaised,
                    onSurfaceVariant = colors.textSecondary,
                    surfaceContainer = colors.surface,
                    surfaceContainerLow = colors.surface,
                    surfaceContainerHigh = colors.surfaceRaised,
                    surfaceContainerHighest = colors.surfaceRaised,
                    surfaceContainerLowest = colors.background,
                    surfaceTint = colors.accent,
                    outline = colors.border,
                    outlineVariant = colors.border,
                    error = colors.loss,
                    onError = colors.background,
                    errorContainer = colors.dangerSurface,
                    onErrorContainer = colors.text,
                ),
            typography = TradebotTheme.typography.material,
            shapes = TradebotTheme.shapes.material,
            content = content,
        )
    }
}

object TradebotTheme {
    val colors: TradebotColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography = TradebotTypography()
    val shapes = TradebotShapes()
}

private val LocalAppColors = staticCompositionLocalOf { DarkTradebotColors }
