package ru.bolotov.feature.positions.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.positions.domain.interactor.PositionsInteractor

interface PositionsDependencies : Dependencies {

    val analyticManager: AnalyticManager
    val positionsInteractor: PositionsInteractor
    val tradingStateInteractor: TradingStateInteractor
}
