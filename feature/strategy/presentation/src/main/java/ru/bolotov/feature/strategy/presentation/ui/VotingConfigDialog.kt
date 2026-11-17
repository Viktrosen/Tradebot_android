package ru.bolotov.feature.strategy.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.TradebotDialog
import ru.bolotov.core.uikit.components.TradebotDialogButton
import ru.bolotov.core.uikit.components.TradebotSlider
import ru.bolotov.core.uikit.dimensions.TradebotDimens
import ru.bolotov.feature.strategy.domain.model.VotingWeights
import ru.bolotov.feature.strategy.domain.model.VotingWeightsError
import ru.bolotov.tradebot.feature.strategy.presentation.R

/** Renders the MVI-owned draft without rebuilding a hard-coded four-indicator payload. */
@Composable
internal fun VotingConfigDialog(
    draft: VotingWeights,
    isSaving: Boolean,
    onParticipantChange: (String, Boolean) -> Unit,
    onWeightChange: (String, Int) -> Unit,
    onConfirm: (Map<String, Int>) -> Unit,
    onDismiss: () -> Unit,
) {
    TradebotDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.strategies_voting_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(TradebotDimens.itemGap)) {
                Text(
                    text = stringResource(R.string.strategies_voting_participants_hint),
                    style = MaterialTheme.typography.bodySmall,
                )
                VotingWeights.SUPPORTED_PARTICIPANTS.forEach { participant ->
                    val weight = draft.values[participant]
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = weight != null,
                                enabled = !isSaving,
                                onCheckedChange = { enabled -> onParticipantChange(participant, enabled) },
                            )
                            Text(text = if (participant == "SUPER_TREND") "SuperTrend" else participant)
                        }
                        if (weight != null) {
                            WeightSliderRow(
                                value = weight,
                                onValueChange = { value -> if (!isSaving) onWeightChange(participant, value) },
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.strategies_total_weight, draft.totalWeight),
                    style = MaterialTheme.typography.bodySmall,
                )
                draft.validationError?.let { error ->
                    Text(
                        text = votingValidationMessage(error = error, draft = draft),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TradebotDialogButton(
                enabled = draft.isValid && !isSaving,
                onClick = { if (draft.isValid && !isSaving) onConfirm(draft.toPayload()) },
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
private fun votingValidationMessage(error: VotingWeightsError, draft: VotingWeights): String = when (error) {
    VotingWeightsError.TOO_FEW_PARTICIPANTS -> stringResource(R.string.strategies_voting_minimum_participants)
    VotingWeightsError.INVALID_WEIGHT -> stringResource(R.string.strategies_voting_invalid_weight)
    VotingWeightsError.UNSUPPORTED_PARTICIPANTS -> stringResource(
        R.string.strategies_voting_unsupported_participants,
        draft.unsupportedParticipants.joinToString(),
    )
}

@Composable
private fun WeightSliderRow(value: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TradebotSlider(
            value = value.coerceIn(
                minimumValue = VotingWeights.MIN_WEIGHT,
                maximumValue = VotingWeights.MAX_WEIGHT,
            ).toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = VotingWeights.MIN_WEIGHT.toFloat()..VotingWeights.MAX_WEIGHT.toFloat(),
            steps = VotingWeights.MAX_WEIGHT - VotingWeights.MIN_WEIGHT - 1,
            modifier = Modifier.weight(1f),
        )
        Text(text = value.toString(), modifier = Modifier.width(30.dp))
    }
}

@Preview
@Composable
private fun VotingConfigDialogPreview() {
    TradebotTheme {
        VotingConfigDialog(
            draft = VotingWeights.from(
                mapOf("EMA" to 3, "RSI" to 2, "MACD" to 2, "BB" to 2, "SUPER_TREND" to 10, "VWAP" to 7)
            ),
            isSaving = false,
            onParticipantChange = { _, _ -> },
            onWeightChange = { _, _ -> },
            onConfirm = {},
            onDismiss = {},
        )
    }
}
