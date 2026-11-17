package ru.bolotov.features.more.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor
import ru.bolotov.feature.more.domain.interactor.AppInfoInteractor
import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor
import ru.bolotov.feature.more.domain.interactor.ThemeInteractor

interface MoreDependencies : Dependencies {
    val authInteractor: AuthInteractor
    val analyticManager: AnalyticManager
    val emergencyCloseInteractor: EmergencyCloseInteractor
    val themeInteractor: ThemeInteractor
    val appInfoInteractor: AppInfoInteractor
}
