package ru.bolotov.tradebot.navigation.data

import kotlinx.serialization.Serializable
import ru.bolotov.feature.main.route.MainGraph

@Serializable data class PendingAppLink(val requestId: String, val destination: AppLink)

/** Main accepts navigation arguments without depending on app or Firebase types. */
fun PendingAppLink.toMainRoute(): MainGraph.Root =
    when (val target = destination) {
        is AppLink.PositionDetails ->
            MainGraph.Root(initialPositionId = target.positionId, launchId = requestId)
    }
