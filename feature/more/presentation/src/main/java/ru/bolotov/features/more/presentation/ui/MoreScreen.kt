package ru.bolotov.features.more.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.core.uikit.components.*
import ru.bolotov.features.more.presentation.core.Action
import ru.bolotov.features.more.presentation.core.Effect
import ru.bolotov.features.more.presentation.core.MoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreScreen(
    viewModel: MoreViewModel,
    onNavigateToInstruments: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.ShowToast -> {
                // TODO: Show toast implementation
            }
            is Effect.ShowMessage -> {
                snackbarHostState.showSnackbar(context.getString(effect.messageResId))
            }
            is Effect.ShowError -> {
                // TODO: Show error dialog
            }
            is Effect.NavigateToInstruments -> onNavigateToInstruments()
            is Effect.NavigateToHistory -> onNavigateToHistory()
            is Effect.NavigateToSettings -> onNavigateToSettings()
            is Effect.NavigateToAbout -> onNavigateToAbout()
            is Effect.Logout -> onLogout()
        }
    }

    MoreContent(state, onAction = viewModel::handleAction, snackbarHostState = snackbarHostState)

    if (state.isEmergencyCloseDialogVisible) {
        EmergencyCloseDialog(
            onConfirm = { viewModel.handleAction(Action.ConfirmEmergencyClose) },
            onDismiss = { viewModel.handleAction(Action.DismissEmergencyCloseDialog) },
        )
    }
}
