package ru.bolotov.feature.positionDetails.presentation.core

data class UiState(
    val details: PositionDetailsUiModel? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isExplanationExpanded: Boolean = false,
    val isEntryExplanationExpanded: Boolean = false,
    val closeState: CloseUiState = CloseUiState.Idle,
    val error: Boolean = false,
)

internal sealed interface Action {
    data object LoadData : Action

    data object Refresh : Action

    data object ScreenResumed : Action

    data object ToggleExplanation : Action

    data object ToggleEntryExplanation : Action

    data object RequestClose : Action

    data object ConfirmClose : Action

    data object DismissCloseDialog : Action

    data object BackClicked : Action
}

internal sealed interface Effect {
    data object NavigateBack : Effect

    data class ShowMessage(val messageRes: Int) : Effect
}
