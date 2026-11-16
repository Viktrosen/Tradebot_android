package ru.bolotov.feature.positionDetails.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.positionDetails.presentation.core.Action
import ru.bolotov.feature.positionDetails.presentation.core.Effect
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsViewModel

@Composable
internal fun PositionDetailsScreen(viewModel: PositionDetailsViewModel, onBack: () -> Unit) {
    val state by viewModel.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        viewModel.handleAction(Action.ScreenResumed)
    }
    viewModel.collectSideEffect { effect ->
        when (effect) {
            Effect.NavigateBack -> onBack()
            is Effect.ShowMessage ->
                snackbar.showSnackbar(message = context.getString(effect.messageRes))
        }
    }
    PositionDetailsContent(
        state = state,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbar,
    )
}
