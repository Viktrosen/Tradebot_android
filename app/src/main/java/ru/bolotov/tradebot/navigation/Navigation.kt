package ru.bolotov.tradebot.navigation

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.bolotov.core.dependency.destinationsProvider
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.findDestinations
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.feature.main.route.MainEntry
import ru.bolotov.feature.splashScreen.router.SplashGraph
import ru.bolotov.feature.splashScreen.router.SplashScreenEntry
import ru.bolotov.tradebot.di.components.DaggerAppEffectsHandlerComponent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(
    activity: ComponentActivity,
    globalNavController: NavHostController,
    viewModel: AppEffectsHandlerViewModel = injectedViewModel(activity) {
        DaggerAppEffectsHandlerComponent.builder().dependencies(activity.findDependencies())
            .build().viewModel
    }
) {
    val splashScreen = activity.findDestinations<SplashScreenEntry>()
    val mainEntry = activity.findDestinations<MainEntry>()
    /*val subscriptionEntry = activity.findDestinations<SubscriptionEntry>()*/

    Box(
        modifier = Modifier.fillMaxSize().background(TradebotTheme.colors.base.White),
    ) {
        NavHost(
            startDestination = SplashGraph.Root, navController = globalNavController
        ) {
            with(splashScreen) {
                animatedComposable(
                    globalNavController = globalNavController,
                    navController = globalNavController,
                    destinations = activity.destinationsProvider
                )
            }

            with(mainEntry) {
                animatedComposable(
                    globalNavController = globalNavController,
                    navController = globalNavController,
                    destinations = activity.destinationsProvider
                )
            }
        }
    }
}