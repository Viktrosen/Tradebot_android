package ru.bolotov.feature.positions.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable
sealed interface PositionsGraph {
    @Serializable
    object Positions
    @Serializable
    object Root: PositionsGraph
}

abstract class PositionsEntry : AggregateFeatureEntry {
    override val featureRoute: String
        get() = "positions"
}