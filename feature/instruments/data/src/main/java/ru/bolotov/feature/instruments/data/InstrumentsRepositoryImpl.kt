package ru.bolotov.feature.instruments.data

import ru.bolotov.feature.instruments.api.InstrumentsApi
import ru.bolotov.feature.instruments.domain.model.TradingInstrument
import ru.bolotov.feature.instruments.domain.model.InstrumentFilters
import ru.bolotov.feature.instruments.api.InstrumentFiltersResponse
import ru.bolotov.feature.instruments.domain.repository.InstrumentsRepository
import javax.inject.Inject

class InstrumentsRepositoryImpl @Inject constructor(private val api: InstrumentsApi) : InstrumentsRepository {
    override suspend fun getInstruments(): List<TradingInstrument> =
        api.getInstruments().instruments.map { instrument ->
            TradingInstrument(
                id = instrument.id,
                ticker = instrument.ticker,
                name = instrument.name,
                tradingAvailable = instrument.tradingAvailable
            )
        }

    override suspend fun rescanInstruments(): List<TradingInstrument> =
        api.rescanInstruments().instruments.map { instrument ->
            TradingInstrument(
                id = instrument.id,
                ticker = instrument.ticker,
                name = instrument.name,
                tradingAvailable = instrument.tradingAvailable
            )
        }
    override suspend fun getFilters(): InstrumentFilters = api.getFilters().let { InstrumentFilters(it.minDailyVolume, it.minVolatility, it.maxVolatility, it.maxCount) }
    override suspend fun updateFilters(filters: InstrumentFilters) { api.updateFilters(InstrumentFiltersResponse(filters.minDailyVolume, filters.minVolatility, filters.maxVolatility, filters.maxCount)) }
}
