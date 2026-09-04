package ru.bolotov.tradebot.router

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

fun NavBackStackEntry.withLifecycle(navigate: (NavBackStackEntry) -> Unit) {
    if (this.lifecycleIsResumed()) {
        navigate(this)
    }
}