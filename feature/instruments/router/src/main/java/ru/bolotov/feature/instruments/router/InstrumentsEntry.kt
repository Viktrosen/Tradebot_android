package ru.bolotov.feature.instruments.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable sealed interface InstrumentsGraph {
    @Serializable data object Instruments
    @Serializable data object Root : InstrumentsGraph
}

abstract class InstrumentsEntry : AggregateFeatureEntry {
    override val featureRoute: String get() = "instruments"
}
