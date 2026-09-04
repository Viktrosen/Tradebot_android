package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.instruments.presentation.di.InstrumentsDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface InstrumentsDependenciesModule {
    @Binds @IntoMap @DependenciesKey(InstrumentsDependencies::class)
    fun bindInstrumentsDependencies(impl: AppComponent): Dependencies
}
