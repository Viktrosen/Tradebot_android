package ru.bolotov.core.uikit.shapes

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Rounded panel with one clipped top-end corner; mirrors in RTL layouts. */
@Immutable
data class ClippedCornerShape(val cut: Dp = 12.dp, val radius: Dp = 12.dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val c = with(density) { cut.toPx() }.coerceIn(0f, minOf(size.width, size.height) / 2)
        val r = with(density) { radius.toPx() }.coerceIn(0f, minOf(size.width, size.height) / 2)
        val w = size.width
        val h = size.height
        val path =
            Path().apply {
                moveTo(r, 0f)
                lineTo(w - c, 0f)
                lineTo(w, c)
                lineTo(w, h - r)
                quadraticTo(w, h, w - r, h)
                lineTo(r, h)
                quadraticTo(0f, h, 0f, h - r)
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
                close()
            }
        if (layoutDirection == LayoutDirection.Rtl) {
            path.transform(
                androidx.compose.ui.graphics.Matrix().apply {
                    translate(w, 0f)
                    scale(-1f, 1f)
                }
            )
        }
        return Outline.Generic(path)
    }
}
