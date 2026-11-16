package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.positionDetails.presentation.di.PositionDetailsDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface PositionDetailsDependenciesModule {
    @Binds
    @IntoMap
    @DependenciesKey(PositionDetailsDependencies::class)
    fun bindPositionDetailsDependencies(impl: AppComponent): Dependencies
}
