package ru.bolotov.feature.auth.presentation

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectAsState
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.feature.auth.router.AuthEntry
import ru.bolotov.feature.auth.router.AuthGraph
import ru.bolotov.tradebot.router.Destinations
import javax.inject.Inject

class AuthEntryCore @Inject constructor() : AuthEntry() {
    override fun NavGraphBuilder.animatedComposable(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations,
        enterTransition: (() -> EnterTransition?)?,
        exitTransition: (() -> ExitTransition?)?,
        popEnterTransition: (() -> EnterTransition?)?,
        popExitTransition: (() -> ExitTransition?)?
    ) {
        composable<AuthGraph.Root>(
            enterTransition = { enterTransition?.invoke() },
            exitTransition = { exitTransition?.invoke() },
            popEnterTransition = { popEnterTransition?.invoke() },
            popExitTransition = { popExitTransition?.invoke() }
        ) {
            val activity = LocalContext.current as Activity
            val viewModel = injectedViewModel {
                DaggerAuthComponent.builder().dependencies(activity.findDependencies()).build().viewModel
            }
            val state by viewModel.collectAsState()
            AuthScreen(state, viewModel::handle)
        }
    }
}
