package ru.bolotov.feature.positionDetails.router

import ru.bolotov.tradebot.router.AggregateFeatureEntry

abstract class PositionDetailsEntry : AggregateFeatureEntry {
    override val featureRoute: String
        get() = "positionDetails"
}
