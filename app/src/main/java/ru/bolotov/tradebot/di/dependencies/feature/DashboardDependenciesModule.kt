package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.dashboard.presentation.di.DashboardDependencies
import ru.bolotov.feature.splashScreen.presentation.di.SplashScreenDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface DashboardDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(DashboardDependencies::class)
    fun bindDashboardDependenciesModule(impl: AppComponent): Dependencies
}