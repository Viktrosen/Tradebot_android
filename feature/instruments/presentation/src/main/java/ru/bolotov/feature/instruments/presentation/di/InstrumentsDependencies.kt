package ru.bolotov.feature.instruments.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.instruments.domain.interactor.InstrumentsInteractor

interface InstrumentsDependencies : Dependencies {
    val instrumentsInteractor: InstrumentsInteractor
    val tradingStateInteractor: TradingStateInteractor
}
