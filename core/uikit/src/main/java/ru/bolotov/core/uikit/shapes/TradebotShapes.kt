package ru.bolotov.core.uikit.shapes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
class TradebotShapes {
    val badge = RoundedCornerShape(6.dp)
    val card = RoundedCornerShape(16.dp)
    val clipped = ClippedCornerShape()
    val material =
        Shapes(
            extraSmall = RoundedCornerShape(6.dp),
            small = RoundedCornerShape(10.dp),
            medium = card,
            large = RoundedCornerShape(20.dp),
            extraLarge = RoundedCornerShape(24.dp),
        )
}
