package ru.bolotov.feature.strategy.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable
sealed interface StrategyGraph {
    @Serializable
    data object Strategy
    @Serializable
    data object Root: StrategyGraph
}

abstract class StrategyEntry : AggregateFeatureEntry {
    override val featureRoute: String
        get() = "strategy"
}