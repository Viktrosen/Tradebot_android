package ru.bolotov.feature.positionDetails.presentation.core

sealed interface CloseUiState {
    data object Idle : CloseUiState

    data object Confirming : CloseUiState

    data object Submitting : CloseUiState

    data object AwaitingConfirmation : CloseUiState

    data object VerifyingResult : CloseUiState
}
