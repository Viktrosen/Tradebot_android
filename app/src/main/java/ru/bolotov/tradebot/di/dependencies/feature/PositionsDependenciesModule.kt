package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.positions.presentation.di.PositionsDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface PositionsDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(PositionsDependencies::class)
    fun bindPositionsDependenciesModule(impl: AppComponent): Dependencies
}