package ru.bolotov.feature.more.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable
sealed interface MoreGraph {
    @Serializable
    data object More

    @Serializable
    data object Root: MoreGraph
}

abstract class MoreEntry : AggregateFeatureEntry {
    override val featureRoute: String
        get() = "more"
}