package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.dashboard.presentation.di.DashboardDependencies
import ru.bolotov.features.more.presentation.di.MoreDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface MoreDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(MoreDependencies::class)
    fun bindMoreDependenciesModule(impl: AppComponent): Dependencies
}