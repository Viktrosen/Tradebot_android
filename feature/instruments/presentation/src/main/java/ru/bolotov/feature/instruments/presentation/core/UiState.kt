package ru.bolotov.feature.instruments.presentation.core

data class UiState(
    val isLoading: Boolean = false,
    val isRescanning: Boolean = false,
    val instruments: List<InstrumentUi> = emptyList(),
    val filters: InstrumentFiltersUi? = null,
    val showFiltersDialog: Boolean = false,
    val errorMessage: String? = null
)
data class InstrumentUi(
    val id: String,
    val ticker: String,
    val name: String,
    val tradingAvailable: Boolean
)
data class InstrumentFiltersUi(val minDailyVolume: Long, val minVolatility: Double, val maxVolatility: Double, val maxCount: Int)

sealed interface Effect { data class ShowError(val message: String) : Effect; data class ShowToast(val message: String) : Effect }
sealed interface Action { data object Load : Action; data object Rescan : Action; data object ShowFilters : Action; data object DismissFilters : Action; data class SaveFilters(val filters: InstrumentFiltersUi) : Action }
