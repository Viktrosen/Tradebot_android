package ru.bolotov.feature.auth.router

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthGraph {
    @Serializable
    data object Root : AuthGraph
}
