package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.dashboard.presentation.di.DashboardDependencies
import ru.bolotov.feature.risk.presentation.di.RiskDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface RiskDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(RiskDependencies::class)
    fun bindRiskDependenciesModule(impl: AppComponent): Dependencies
}