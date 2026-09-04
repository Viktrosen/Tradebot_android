package ru.bolotov.feature.dashboard.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.dashboard.domain.interactor.DashboardInteractor

interface DashboardDependencies : Dependencies {
    val dashboardInteractor: DashboardInteractor
    val analyticManager: AnalyticManager
    val riskInteractor: RiskInteractor
    val tradingStateInteractor: TradingStateInteractor
}
