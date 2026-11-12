package ru.bolotov.tradebot.di.dependencies

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.tradebot.di.components.AppComponent
import ru.bolotov.tradebot.di.components.AppNavigationDependencies

@Module
interface AppNavigationDependenciesModule {
    @Binds
    @IntoMap
    @DependenciesKey(AppNavigationDependencies::class)
    fun appNavigationDependencies(component: AppComponent): Dependencies
}
