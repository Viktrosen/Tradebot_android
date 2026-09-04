package ru.bolotov.feature.splashScreen.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.ComposableFeatureEntry

@Serializable
sealed interface SplashGraph {
    @Serializable
    data object Root: SplashGraph
}

abstract class SplashScreenEntry : ComposableFeatureEntry {
    final override val featureRoute: String
        get() = "splashscreen"
}