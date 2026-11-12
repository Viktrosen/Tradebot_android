package ru.bolotov.feature.auth.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.ComposableFeatureEntry

/** Registers the login destination; application-wide session navigation belongs to app. */
abstract class AuthEntry : ComposableFeatureEntry {
    final override val featureRoute: String
        get() = "auth"
}
