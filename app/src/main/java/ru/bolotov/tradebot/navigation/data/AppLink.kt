package ru.bolotov.tradebot.navigation.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Validated external navigation target, never an executable trading command. */
@Serializable
sealed interface AppLink {
    @Serializable
    @SerialName("position_details")
    data class PositionDetails(val positionId: String) : AppLink
}
