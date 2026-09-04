package ru.bolotov.feature.main.route

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.ComposableFeatureEntry

@Serializable
sealed interface MainGraph {
    @Serializable
    object Root: MainGraph
}

abstract class MainEntry : ComposableFeatureEntry {
    override val featureRoute: String
        get() = "main"
}