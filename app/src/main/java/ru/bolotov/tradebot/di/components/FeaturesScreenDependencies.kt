package ru.bolotov.tradebot.di.components

import ru.bolotov.feature.auth.presentation.di.AuthDependencies
import ru.bolotov.feature.dashboard.presentation.di.DashboardDependencies
import ru.bolotov.feature.instruments.presentation.di.InstrumentsDependencies
import ru.bolotov.feature.main.presentation.di.MainDependencies
import ru.bolotov.feature.positionDetails.presentation.di.PositionDetailsDependencies
import ru.bolotov.feature.positions.presentation.di.PositionsDependencies
import ru.bolotov.feature.risk.presentation.di.RiskDependencies
import ru.bolotov.feature.splashScreen.presentation.di.SplashScreenDependencies
import ru.bolotov.feature.strategy.presentation.di.StrategyDependencies
import ru.bolotov.features.more.presentation.di.MoreDependencies

interface FeaturesScreenDependencies :
    PositionDetailsDependencies,
    AppNavigationDependencies,
    AuthDependencies,
    MainDependencies,
    AppEffectsHandlerDependencies,
    SplashScreenDependencies,
    DashboardDependencies,
    InstrumentsDependencies,
    PositionsDependencies,
    StrategyDependencies,
    RiskDependencies,
    MoreDependencies
