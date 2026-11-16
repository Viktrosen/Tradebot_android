package ru.bolotov.feature.main.route

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.ComposableFeatureEntry

@Serializable
sealed interface MainGraph {
    @Serializable
    data class Root(val initialPositionId: String? = null, val launchId: String? = null) :
        MainGraph
}

abstract class MainEntry : ComposableFeatureEntry {
    override val featureRoute: String
        get() = "main"
}
