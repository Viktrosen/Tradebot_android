package ru.bolotov.core.uikit.shapes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.dp

@Stable
data class TradebotShapes(
    val s: RoundedCornerShape = RoundedCornerShape(8.dp),
    val m: RoundedCornerShape = RoundedCornerShape(16.dp),
    val l: RoundedCornerShape = RoundedCornerShape(20.dp),
    val xl: RoundedCornerShape = RoundedCornerShape(24.dp),
    val checkbox: RoundedCornerShape = RoundedCornerShape(2.dp),
)