package ru.bolotov.tradebot.di.dependencies

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.auth.presentation.di.AuthDependencies
import ru.bolotov.tradebot.di.components.AppComponent

@Module
interface AuthDependenciesModule {
    @Binds @IntoMap @DependenciesKey(AuthDependencies::class)
    fun authDependencies(component: AppComponent): Dependencies
}
