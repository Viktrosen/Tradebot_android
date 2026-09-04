package ru.bolotov.feature.positions.presentation.core

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
import ru.bolotov.feature.positions.presentation.di.DaggerPositionsComponent
import ru.bolotov.feature.positions.presentation.di.DaggerPositionsFeatureComponent
import ru.bolotov.feature.positions.presentation.di.PositionsComponent
import ru.bolotov.feature.positions.presentation.di.PositionsFeatureComponent
import ru.bolotov.feature.positions.presentation.ui.PositionsScreen
import ru.bolotov.feature.positions.router.PositionsEntry
import ru.bolotov.feature.positions.router.PositionsGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class PositionsEntryCore @Inject constructor() : PositionsEntry(),
    RootComponentHolder<PositionsFeatureComponent> {
    override val rootRoute: String
        get() = PositionsGraph.Root::class.qualifiedName ?: "dashboard_root"

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): PositionsFeatureComponent = rememberScoped(storeOwner = rootEntry) {
        DaggerPositionsFeatureComponent.builder().build()
    }

    private var component: PositionsComponent? = null

    override val deepLinks: List<NavDeepLink> = listOf()

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    ) {
        navigation<PositionsGraph.Root>(startDestination = PositionsGraph.Positions) {
            composable<PositionsGraph.Positions> { backStackEntry ->
                PositionsScreen(
                    viewModel = getDashboardViewModel(
                        backStackEntry = backStackEntry,
                        navController = navController
                    )
                )
            }
        }
    }

    @Composable
    private fun getDashboardViewModel(
        backStackEntry: NavBackStackEntry,
        navController: NavHostController
    ): PositionsViewModel {
        val rootEntry = backStackEntry.rememberBackStackEntry<PositionsGraph.Root>(navController)
        val featureComponent = component ?: getComponent(backStackEntry, rootEntry)
        return injectedViewModel(viewModelStoreOwner = rootEntry) {
            featureComponent.positionsViewModel
        }
    }

    @Composable
    private fun getComponent(
        backStackEntry: NavBackStackEntry,
        rootEntry: NavBackStackEntry,
    ): PositionsComponent {
        val context = LocalContext.current
        val rootComponent = rootComponent(rootEntry, backStackEntry.arguments, context)
        return DaggerPositionsComponent.builder()
            .component(rootComponent)
            .dependencies((context as Activity).findDependencies())
            .build()
            .also { component = it }
    }

}