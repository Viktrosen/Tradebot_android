package ru.bolotov.feature.risk.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.bolotov.core.uikit.components.TradebotSlider

/** Controlled by MVI state, so reset and refreshed values also move the thumb. */
@Composable
fun RiskSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    formatValue: @Composable (Float) -> String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        TradebotSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatValue(valueRange.start), style = MaterialTheme.typography.labelSmall)
            Text(formatValue(valueRange.endInclusive), style = MaterialTheme.typography.labelSmall)
        }
    }
}
