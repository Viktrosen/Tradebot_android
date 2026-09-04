package ru.bolotov.tradebot.di.dependencies.feature

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.bolotov.tradebot.di.components.AppComponent
import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.core.dependency.DependenciesKey
import ru.bolotov.feature.splashScreen.presentation.di.SplashScreenDependencies

@Module
interface SplashDependenciesModule {

    @Binds
    @IntoMap
    @DependenciesKey(SplashScreenDependencies::class)
    fun bindSlashScreenDependenciesModule(impl: AppComponent): Dependencies
}