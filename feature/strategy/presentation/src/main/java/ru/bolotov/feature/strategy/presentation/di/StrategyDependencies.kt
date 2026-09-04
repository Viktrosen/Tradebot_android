package ru.bolotov.feature.strategy.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor

interface StrategyDependencies : Dependencies {

    val analyticManager: AnalyticManager
    val strategyInteractor: StrategyInteractor
}
