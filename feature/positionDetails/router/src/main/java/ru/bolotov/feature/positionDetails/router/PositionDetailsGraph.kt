package ru.bolotov.feature.positionDetails.router

import kotlinx.serialization.Serializable

@Serializable
sealed interface PositionDetailsGraph {
    @Serializable
    data class Root(val positionId: String) : PositionDetailsGraph

    @Serializable data object Details : PositionDetailsGraph
}
