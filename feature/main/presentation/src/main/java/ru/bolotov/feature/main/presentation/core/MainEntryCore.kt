package ru.bolotov.feature.main.presentation.core

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectAsState
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.feature.main.presentation.di.DaggerHomeComponent
import ru.bolotov.feature.main.presentation.ui.MainScreenHost
import ru.bolotov.feature.main.route.MainEntry
import ru.bolotov.feature.main.route.MainGraph
import ru.bolotov.tradebot.router.Destinations
import javax.inject.Inject

class MainEntryCore @Inject constructor() : MainEntry() {

    override fun NavGraphBuilder.animatedComposable(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations,
        enterTransition: (() -> EnterTransition?)?,
        exitTransition: (() -> ExitTransition?)?,
        popEnterTransition: (() -> EnterTransition?)?,
        popExitTransition: (() -> ExitTransition?)?
    ) {
        composable<MainGraph.Root>(
            enterTransition = { enterTransition?.invoke() },
            exitTransition = { exitTransition?.invoke() },
            popEnterTransition = { popEnterTransition?.invoke() },
            popExitTransition = { popExitTransition?.invoke() }
        ) { backstackEntry ->
            val context = LocalContext.current
            val activity = remember { context as? Activity }
            val viewModel: MainHostViewModel = injectedViewModel {
                DaggerHomeComponent
                    .builder()
                    .dependencies((context as Activity).findDependencies())
                    .build()
                    .viewModel
            }
            val state by viewModel.collectAsState()

            activity?.let {
                MainScreenHost(
                    state = state,
                    activity = it,
                    globalNavController = globalNavController,
                    action = viewModel::handleAction,
                )
            }
        }
    }
}