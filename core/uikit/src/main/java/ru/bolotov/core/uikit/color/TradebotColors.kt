package ru.bolotov.core.uikit.color

import androidx.compose.runtime.Stable
import ru.bolotov.core.uikit.theme.color.ColorPalette

@Stable
data class TradebotColors(
    val base: ColorPalette.Base,
    val additional: ColorPalette.Additional,
    val gray: ColorPalette.GrayTones,
    val red: ColorPalette.RedTones,
    val text: ColorPalette.Text,
    val icon: ColorPalette.IconColors,
    val inputsIcons: ColorPalette.InputsIcons,
    val stroke: ColorPalette.StrokeColors,
    val main: ColorPalette.Main,
    val smsInputColors: ColorPalette.SmsInputColors,
    val background: ColorPalette.BackgroundColors,
    val button: ColorPalette.ButtonColors,
)

internal val AppColorDefault = TradebotColors(
    base = ColorPalette.Base,
    additional = ColorPalette.Additional,
    gray = ColorPalette.GrayTones,
    red = ColorPalette.RedTones,
    text = ColorPalette.Text,
    icon = ColorPalette.IconColors,
    inputsIcons = ColorPalette.InputsIcons,
    stroke = ColorPalette.StrokeColors,
    main = ColorPalette.Main,
    smsInputColors = ColorPalette.SmsInputColors,
    background = ColorPalette.BackgroundColors,
    button = ColorPalette.ButtonColors,
)