package ru.bolotov.tradebot.navigation

import androidx.activity.ComponentActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.bolotov.core.dependency.destinationsProvider
import ru.bolotov.core.dependency.findDestinations
import ru.bolotov.feature.auth.router.AuthEntry
import ru.bolotov.feature.auth.router.AuthGraph
import ru.bolotov.feature.main.route.MainEntry

/** Registers ordinary feature destinations. MainActivity coordinates session-driven transitions. */
@Composable
fun Navigation(activity: ComponentActivity, globalNavController: NavHostController) {
    val authEntry = activity.findDestinations<AuthEntry>()
    val mainEntry = activity.findDestinations<MainEntry>()

    NavHost(
        startDestination = AuthGraph.Root,
        navController = globalNavController,
        // Drop protected compositions immediately on logout, without an outgoing animation.
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        with(authEntry) {
            animatedComposable(globalNavController, globalNavController, activity.destinationsProvider)
        }
        with(mainEntry) {
            animatedComposable(globalNavController, globalNavController, activity.destinationsProvider)
        }
    }
}
