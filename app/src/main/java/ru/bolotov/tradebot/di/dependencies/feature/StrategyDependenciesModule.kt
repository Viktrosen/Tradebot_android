package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.dashboard.presentation.di.DashboardDependencies
import ru.bolotov.feature.strategy.presentation.di.StrategyDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface StrategyDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(StrategyDependencies::class)
    fun bindStrategyDependenciesModule(impl: AppComponent): Dependencies
}