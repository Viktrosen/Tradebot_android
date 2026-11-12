package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotDialog
import ru.bolotov.core.uikit.components.TradebotDialogButton
import ru.bolotov.core.uikit.components.TradebotSlider
import ru.bolotov.tradebot.feature.strategy.presentation.R

@Composable
fun VotingConfigDialog(
    initialWeights: Map<String, Int> = mapOf("EMA" to 1, "RSI" to 1, "MACD" to 1, "BB" to 1),
    onConfirm: (Map<String, Int>) -> Unit,
    onDismiss: () -> Unit,
) {
    var emaWeight by remember(initialWeights) { mutableStateOf(initialWeights["EMA"] ?: 1) }
    var rsiWeight by remember(initialWeights) { mutableStateOf(initialWeights["RSI"] ?: 1) }
    var macdWeight by remember(initialWeights) { mutableStateOf(initialWeights["MACD"] ?: 1) }
    var bbWeight by remember(initialWeights) { mutableStateOf(initialWeights["BB"] ?: 1) }

    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.strategies_voting_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WeightSliderRow("EMA", value = emaWeight, onValueChange = { emaWeight = it })
                WeightSliderRow("RSI", value = rsiWeight, onValueChange = { rsiWeight = it })
                WeightSliderRow("MACD", value = macdWeight, onValueChange = { macdWeight = it })
                WeightSliderRow("BB", value = bbWeight, onValueChange = { bbWeight = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        stringResource(
                            R.string.strategies_total_weight,
                            emaWeight + rsiWeight + macdWeight + bbWeight,
                        ),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TradebotDialogButton(
                onClick = {
                    val weights =
                        mapOf(
                            "EMA" to emaWeight,
                            "RSI" to rsiWeight,
                            "MACD" to macdWeight,
                            "BB" to bbWeight,
                        )
                    onConfirm(weights)
                }
            ) {
                Text(stringResource(R.string.strategies_apply))
            }
        },
        dismissButton = {
            TradebotDialogButton(onClick = onDismiss, outlined = true) {
                Text(stringResource(R.string.strategies_cancel))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    )
}

@Composable
private fun WeightSliderRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.width(50.dp))
        TradebotSlider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.weight(1f),
        )
        Text(text = value.toString(), modifier = Modifier.width(30.dp))
    }
}

@Preview
@Composable
private fun VotingConfigDialogPreview() {
    TradebotTheme { VotingConfigDialog(onConfirm = {}, onDismiss = {}) }
}
