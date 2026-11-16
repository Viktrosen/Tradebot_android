package ru.bolotov.feature.positions.presentation.ui

import ru.bolotov.core.uikit.format.formatExplanationNumbers
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.positions.presentation.core.Action
import ru.bolotov.feature.positions.presentation.core.Effect
import ru.bolotov.feature.positions.presentation.core.PositionsViewModel
import ru.bolotov.tradebot.feature.positions.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PositionsScreen(
    viewModel: PositionsViewModel,
    onOpenPositionDetails: (String) -> Unit,
    onNavigateToInstrument: (String) -> Unit = {},
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.NavigateToPositionDetails -> onOpenPositionDetails(effect.positionId)
            is Effect.ShowToast -> {
                // TODO: Show toast implementation
            }

            is Effect.ShowError -> {
                // TODO: Show error dialog
            }

            is Effect.Refresh -> {
                // Handle refresh complete
            }
        }
    }

    PositionsContent(
        state,
        onAction = viewModel::handleAction,
        onNavigateToInstrument = onNavigateToInstrument,
    )

    // Close confirmation dialog
    state.closingPosition?.let { position ->
        ClosePositionDialog(
            positionName = position.instrumentName,
            onConfirm = { viewModel.handleAction(Action.ConfirmClose) },
            onDismiss = { viewModel.handleAction(Action.DismissCloseDialog) },
        )
    }

    state.infoPosition?.let { position ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.handleAction(Action.DismissPositionInfo) }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(
                    text =
                        stringResource(
                            if (position.isClosed) {
                                R.string.position_close_reason_title
                            } else {
                                R.string.position_ai_explanation_title
                            }
                        ),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(12.dp))
                Text(position.infoExplanation.orEmpty().formatAiExplanationNumbers())
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/** Keeps AI prose intact while making overly precise decimal values readable. */
internal fun String.formatAiExplanationNumbers(): String =
    formatExplanationNumbers()
