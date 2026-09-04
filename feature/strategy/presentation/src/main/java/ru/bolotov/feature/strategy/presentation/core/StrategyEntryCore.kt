package ru.bolotov.feature.strategy.presentation.core

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.core.dependency.rememberScoped
import ru.bolotov.feature.strategy.presentation.di.DaggerStrategyComponent
import ru.bolotov.feature.strategy.presentation.di.DaggerStrategyFeatureComponent
import ru.bolotov.feature.strategy.presentation.di.StrategyComponent
import ru.bolotov.feature.strategy.presentation.di.StrategyFeatureComponent
import ru.bolotov.feature.strategy.presentation.ui.StrategyScreen
import ru.bolotov.feature.strategy.router.StrategyEntry
import ru.bolotov.feature.strategy.router.StrategyGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class StrategyEntryCore @Inject constructor() : StrategyEntry(),
    RootComponentHolder<StrategyFeatureComponent> {
    override val rootRoute: String
        get() = StrategyGraph.Root::class.qualifiedName ?: "dashboard_root"

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): StrategyFeatureComponent = rememberScoped(storeOwner = rootEntry) {
        DaggerStrategyFeatureComponent.builder().build()
    }

    private var component: StrategyComponent? = null

    override val deepLinks: List<NavDeepLink> = listOf()

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    ) {
        navigation<StrategyGraph.Root>(startDestination = StrategyGraph.Strategy) {
            composable<StrategyGraph.Strategy> { backStackEntry ->
                StrategyScreen(
                    viewModel = getStrategyViewModel(
                        backStackEntry = backStackEntry,
                        navController = navController
                    )
                )
            }
        }
    }

    @Composable
    private fun getStrategyViewModel(
        backStackEntry: NavBackStackEntry,
        navController: NavHostController
    ): StrategyViewModel {
        val rootEntry = backStackEntry.rememberBackStackEntry<StrategyGraph.Root>(navController)
        val featureComponent = component ?: getComponent(backStackEntry, rootEntry)
        return injectedViewModel(viewModelStoreOwner = rootEntry) {
            featureComponent.strategyViewModel
        }
    }

    @Composable
    private fun getComponent(
        backStackEntry: NavBackStackEntry,
        rootEntry: NavBackStackEntry,
    ): StrategyComponent {
        val context = LocalContext.current
        val rootComponent = rootComponent(rootEntry, backStackEntry.arguments, context)
        return DaggerStrategyComponent.builder()
            .component(rootComponent)
            .dependencies((context as Activity).findDependencies())
            .build()
            .also { component = it }
    }

}