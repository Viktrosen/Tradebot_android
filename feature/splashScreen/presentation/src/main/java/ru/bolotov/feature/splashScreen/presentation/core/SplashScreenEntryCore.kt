package ru.bolotov.feature.splashScreen.presentation.core

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.feature.main.route.MainGraph
import ru.bolotov.feature.splashScreen.presentation.di.DaggerSplashScreenComponent
import ru.bolotov.feature.splashScreen.presentation.ui.SplashScreen
import ru.bolotov.feature.splashScreen.router.SplashGraph
import ru.bolotov.feature.splashScreen.router.SplashScreenEntry
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.withLifecycle
import javax.inject.Inject

class SplashScreenEntryCore @Inject constructor() : SplashScreenEntry() {

    override fun NavGraphBuilder.animatedComposable(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations,
        enterTransition: (() -> EnterTransition?)?,
        exitTransition: (() -> ExitTransition?)?,
        popEnterTransition: (() -> EnterTransition?)?,
        popExitTransition: (() -> ExitTransition?)?
    ) {
        composable<SplashGraph.Root>(
            enterTransition = { enterTransition?.invoke() },
            exitTransition = { exitTransition?.invoke() },
            popEnterTransition = { popEnterTransition?.invoke() },
            popExitTransition = { popExitTransition?.invoke() }
        ) {
            val context = LocalContext.current
            val viewModel = injectedViewModel {
                DaggerSplashScreenComponent
                    .builder()
                    .dependencies((context as Activity).findDependencies())
                    .build()
                    .splashViewModel
            }
            SplashScreen(
                viewModel = viewModel,
                onNavigateToMainScreen = {
                    globalNavController.navigateToHomepage()
                }
            )
        }
    }

    private fun NavHostController.navigateToHomepage() {
        this.currentBackStackEntry?.withLifecycle {
            navigate(MainGraph.Root) {
                popUpTo(this@navigateToHomepage.graph.id) { inclusive = true }
            }
        }
    }
}