package ru.bolotov.feature.instruments.domain.repository

import ru.bolotov.feature.instruments.domain.model.TradingInstrument
import ru.bolotov.feature.instruments.domain.model.InstrumentFilters

interface InstrumentsRepository {
    suspend fun getInstruments(): List<TradingInstrument>
    suspend fun rescanInstruments(): List<TradingInstrument>
    suspend fun getFilters(): InstrumentFilters
    suspend fun updateFilters(filters: InstrumentFilters)
}
