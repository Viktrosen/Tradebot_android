package ru.bolotov.tradebot.di.dependencies

import dagger.Module
import ru.bolotov.tradebot.di.dependencies.feature.AppEffectsHandlerDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.DashboardDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.InstrumentsDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.MainDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.MoreDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.PositionsDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.RiskDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.SplashDependenciesModule
import ru.bolotov.tradebot.di.dependencies.feature.StrategyDependenciesModule

@Module(
    includes = [
        MainDependenciesModule::class,
        AppEffectsHandlerDependenciesModule::class,
        SplashDependenciesModule::class,
        DashboardDependenciesModule::class,
        InstrumentsDependenciesModule::class,
        PositionsDependenciesModule::class,
        StrategyDependenciesModule::class,
        RiskDependenciesModule::class,
        MoreDependenciesModule::class,
    ]
)
interface FeatureDependenciesModule
