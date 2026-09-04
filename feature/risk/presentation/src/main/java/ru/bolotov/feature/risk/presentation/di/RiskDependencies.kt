package ru.bolotov.feature.risk.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor

interface RiskDependencies : Dependencies {

    val analyticManager: AnalyticManager
    val riskInteractor: RiskInteractor
}
