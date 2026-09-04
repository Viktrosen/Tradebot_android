package ru.bolotov.feature.splashScreen.presentation.di

import dagger.Component
import ru.bolotov.core.dependency.SubFeatureScoped
import ru.bolotov.feature.splashScreen.presentation.core.SplashViewModel

@SubFeatureScoped
@Component(dependencies = [SplashScreenDependencies::class])
internal interface SplashScreenComponent {
    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: SplashScreenDependencies): Builder
        fun build(): SplashScreenComponent
    }
    val splashViewModel: SplashViewModel
}
