package ru.bolotov.feature.risk.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable
sealed interface RiskGraph {
    @Serializable
    data object Risk
    @Serializable
    data object Root: RiskGraph
}

abstract class RiskEntry : AggregateFeatureEntry {
    override val featureRoute: String
        get() = "risk"
}