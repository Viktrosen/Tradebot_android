package ru.bolotov.feature.instruments.domain.core

import ru.bolotov.feature.instruments.domain.interactor.InstrumentsInteractor
import ru.bolotov.feature.instruments.domain.model.TradingInstrument
import ru.bolotov.feature.instruments.domain.model.InstrumentFilters
import ru.bolotov.feature.instruments.domain.repository.InstrumentsRepository

class InstrumentsInteractorImpl(private val repository: InstrumentsRepository) : InstrumentsInteractor {
    override suspend fun getInstruments(): List<TradingInstrument> = repository.getInstruments()
    override suspend fun rescanInstruments(): List<TradingInstrument> = repository.rescanInstruments()
    override suspend fun getFilters(): InstrumentFilters = repository.getFilters()
    override suspend fun updateFilters(filters: InstrumentFilters) = repository.updateFilters(filters)
}
