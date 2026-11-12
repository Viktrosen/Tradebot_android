package ru.bolotov.core.uikit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.bolotov.core.uikit.TradebotTheme

/** Material slider interaction and accessibility, with a thin track and round thumb. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradebotSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier,
) {
    val colors = TradebotTheme.colors
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier,
        colors =
            SliderDefaults.colors(thumbColor = colors.accent, activeTrackColor = colors.accent),
        thumb = {
            Box(
                Modifier.size(20.dp)
                    .background(colors.text, CircleShape)
                    .border(2.dp, colors.accent, CircleShape)
            )
        },
        track = { state ->
            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val fraction =
                    ((state.value - valueRange.start) /
                            (valueRange.endInclusive - valueRange.start))
                        .coerceIn(0f, 1f)
                val start = if (rtl) size.width else 0f
                val end = if (rtl) 0f else size.width
                drawLine(
                    colors.border,
                    Offset(start, center.y),
                    Offset(end, center.y),
                    size.height,
                    StrokeCap.Round,
                )
                drawLine(
                    colors.accent,
                    Offset(start, center.y),
                    Offset(start + (end - start) * fraction, center.y),
                    size.height,
                    StrokeCap.Round,
                )
            }
        },
    )
}
