package ru.bolotov.core.uikit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.bolotov.core.uikit.color.AppColorDefault
import ru.bolotov.core.uikit.color.TradebotColors
import ru.bolotov.core.uikit.shapes.TradebotShapes
import ru.bolotov.core.uikit.typography.TradebotTypography

@Composable
fun TradebotTheme(
    colors: TradebotColors = AppColorDefault,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides TradebotTheme.typography,
        LocalAppShapes provides TradebotTheme.shapes,
        content = content,
    )
}

@Stable
object TradebotTheme {

    val colors: TradebotColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: TradebotTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val shapes: TradebotShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current
}

internal val LocalAppColors = staticCompositionLocalOf<TradebotColors> {
    error("No colors provided")
}

internal val LocalAppTypography = staticCompositionLocalOf { TradebotTypography() }

internal val LocalAppShapes = staticCompositionLocalOf { TradebotShapes() }
