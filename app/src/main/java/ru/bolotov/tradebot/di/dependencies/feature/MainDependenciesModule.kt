package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.main.presentation.di.MainDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface MainDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(MainDependencies::class)
    fun bindMainDependenciesModule(impl: AppComponent): Dependencies
}