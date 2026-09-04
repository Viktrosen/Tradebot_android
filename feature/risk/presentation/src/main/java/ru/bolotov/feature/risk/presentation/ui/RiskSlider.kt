package ru.bolotov.feature.risk.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RiskSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    formatValue: @Composable (Float) -> String,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(value) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatValue(sliderValue),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onValueChange(it)
            },
            valueRange = valueRange,
            steps = steps
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatValue(valueRange.start),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = formatValue(valueRange.endInclusive),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
