package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.tradebot.di.components.AppComponent
import ru.bolotov.tradebot.di.components.AppEffectsHandlerDependencies
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey

@Module
interface AppEffectsHandlerDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(AppEffectsHandlerDependencies::class)
    fun bindAppEffectsHandlerDependenciesModule(impl: AppComponent): Dependencies
}