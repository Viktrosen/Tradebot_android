package ru.bolotov.feature.instruments.presentation.core

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.bolotov.feature.instruments.domain.interactor.InstrumentsInteractor
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.core.network.TradingStateUpdate
import javax.inject.Inject

@Stable
/**
 * MVI-координатор выбранных ботом инструментов, их фильтров и рескана.
 * Доступность торгов обновляет непосредственно из WebSocket-потока.
 */
internal class InstrumentsViewModel @Inject constructor(
    private val instrumentsInteractor: InstrumentsInteractor,
    private val tradingStateInteractor: TradingStateInteractor
) : ViewModel(), ContainerHost<UiState, Effect> {
    override val container: Container<UiState, Effect> = container(UiState())

    init {
        load()
        observeTradingAvailability()
    }

    private fun observeTradingAvailability() {
        viewModelScope.launch {
            tradingStateInteractor.updates().collect { update ->
                if (update is TradingStateUpdate.TradingAvailability) {
                    intent {
                        reduce {
                            state.copy(
                                instruments = state.instruments.map { instrument ->
                                    instrument.copy(
                                        tradingAvailable = update.instruments[
                                            instrument.id
                                        ] ?: instrument.tradingAvailable
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /** Обрабатывает загрузку, рескан и lifecycle диалога фильтров. */
    fun handleAction(action: Action) = when (action) {
        Action.Load -> load(); Action.Rescan -> rescan(); Action.ShowFilters -> intent {
            reduce {
                state.copy(
                    showFiltersDialog = true
                )
            }
        }; Action.DismissFilters -> intent { reduce { state.copy(showFiltersDialog = false) } }; is Action.SaveFilters -> saveFilters(
            action.filters
        )
    }

    private fun load() = intent {
        reduce { state.copy(isLoading = true, errorMessage = null) }
        runCatching {
            instrumentsInteractor.getInstruments().map {
                InstrumentUi(
                    id = it.id,
                    ticker = it.ticker,
                    name = it.name,
                    tradingAvailable = it.tradingAvailable
                )
            } to instrumentsInteractor.getFilters()
        }
            .onSuccess { (instruments, filters) ->
                reduce {
                    state.copy(
                        isLoading = false,
                        instruments = instruments,
                        filters = InstrumentFiltersUi(
                            filters.minDailyVolume,
                            filters.minVolatility,
                            filters.maxVolatility,
                            filters.maxCount
                        )
                    )
                }
            }
            .onFailure { error ->
                val message = error.message ?: "Не удалось загрузить инструменты"
                reduce { state.copy(isLoading = false, errorMessage = message) }
                postSideEffect(Effect.ShowError(message))
            }
    }

    private fun saveFilters(filters: InstrumentFiltersUi) = intent {
        reduce { state.copy(showFiltersDialog = false) }
        runCatching {
            instrumentsInteractor.updateFilters(
                ru.bolotov.feature.instruments.domain.model.InstrumentFilters(
                    filters.minDailyVolume,
                    filters.minVolatility,
                    filters.maxVolatility,
                    filters.maxCount
                )
            )
        }
            .onSuccess {
                reduce {
                    state.copy(
                        filters = filters
                    )
                }; postSideEffect(Effect.ShowToast("Фильтры сохранены. Запустите рескан для применения"))
            }
            .onFailure {
                postSideEffect(
                    Effect.ShowError(
                        it.message ?: "Не удалось сохранить фильтры"
                    )
                )
            }
    }

    private fun rescan() = intent {
        if (state.isRescanning) return@intent
        reduce { state.copy(isRescanning = true) }
        runCatching {
            instrumentsInteractor.rescanInstruments()
                .map {
                    InstrumentUi(
                        id = it.id,
                        ticker = it.ticker,
                        name = it.name,
                        tradingAvailable = it.tradingAvailable
                    )
                }
        }
            .onSuccess { instruments ->
                reduce { state.copy(isRescanning = false, instruments = instruments) }
                postSideEffect(Effect.ShowToast("Список инструментов обновлён"))
            }
            .onFailure { error ->
                val message = error.message ?: "Не удалось выполнить рескан"
                reduce { state.copy(isRescanning = false) }
                postSideEffect(Effect.ShowError(message))
            }
    }
}
