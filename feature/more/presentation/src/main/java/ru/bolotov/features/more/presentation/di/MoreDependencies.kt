package ru.bolotov.features.more.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor

interface MoreDependencies : Dependencies {

    val analyticManager: AnalyticManager
    val emergencyCloseInteractor: EmergencyCloseInteractor
}
