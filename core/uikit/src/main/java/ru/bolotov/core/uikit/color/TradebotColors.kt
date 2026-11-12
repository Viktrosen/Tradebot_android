package ru.bolotov.core.uikit.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Semantic roles shared by Material components and custom trading components. */
@Immutable
data class TradebotColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    val text: Color,
    val textSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val brand: Color,
    val profit: Color,
    val loss: Color,
    val short: Color,
    val dangerSurface: Color,
)

val DarkTradebotColors =
    TradebotColors(
        background = ColorPalette.Ink,
        surface = ColorPalette.Graphite,
        surfaceRaised = ColorPalette.RaisedGraphite,
        border = ColorPalette.Slate,
        text = ColorPalette.White,
        textSecondary = ColorPalette.Mist,
        accent = ColorPalette.Lime,
        onAccent = ColorPalette.Ink,
        brand = ColorPalette.Magenta,
        profit = ColorPalette.Mint,
        loss = ColorPalette.Coral,
        short = ColorPalette.Sky,
        dangerSurface = ColorPalette.Burgundy,
    )

val LightTradebotColors =
    DarkTradebotColors.copy(
        background = ColorPalette.Paper,
        surface = Color.White,
        surfaceRaised = Color(0xFFE7EDF5),
        border = ColorPalette.LightSlate,
        text = ColorPalette.Ink,
        textSecondary = Color(0xFF526176),
        accent = Color(0xFF4B6500),
        onAccent = Color.White,
        profit = Color(0xFF167246),
        loss = Color(0xFFB62E43),
        short = Color(0xFF12658A),
        dangerSurface = Color(0xFFFFEAED),
    )
