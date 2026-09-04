package ru.bolotov.feature.dashboard.presentation.core

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
import ru.bolotov.feature.dashboard.presentation.di.DaggerDashboardComponent
import ru.bolotov.feature.dashboard.presentation.di.DaggerDashboardFeatureComponent
import ru.bolotov.feature.dashboard.presentation.di.DashboardComponent
import ru.bolotov.feature.dashboard.presentation.di.DashboardFeatureComponent
import ru.bolotov.feature.dashboard.presentation.ui.DashboardScreen
import ru.bolotov.feature.dashboard.router.DashboardEntry
import ru.bolotov.feature.dashboard.router.DashboardGraph
import ru.bolotov.feature.instruments.router.InstrumentsGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class DashboardEntryCore @Inject constructor() : DashboardEntry(),
    RootComponentHolder<DashboardFeatureComponent> {

    override val rootRoute: String
        get() = DashboardGraph.Root::class.qualifiedName ?: "dashboard_root"

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): DashboardFeatureComponent = rememberScoped(storeOwner = rootEntry) {
        DaggerDashboardFeatureComponent.builder().build()
    }

    private var component: DashboardComponent? = null

    override val deepLinks: List<NavDeepLink> = listOf()

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    ) {
        navigation<DashboardGraph.Root>(startDestination = DashboardGraph.Dashboard) {
            composable<DashboardGraph.Dashboard> { backStackEntry ->
                DashboardScreen(
                    viewModel = getDashboardViewModel(
                        backStackEntry = backStackEntry,
                        navController = navController,
                    ),
                    onNavigateToPositions = {},
                    onNavigateToRisk = {},
                    onNavigateToStrategy = {},
                    onNavigateToInstruments = { navController.navigate(InstrumentsGraph.Root) }
                )
            }
        }
    }

    @Composable
    private fun getDashboardViewModel(
        backStackEntry: NavBackStackEntry,
        navController: NavHostController
    ): DashboardViewModel {
        // Используем типизированный метод для получения Root entry
        val rootEntry = backStackEntry.rememberBackStackEntry<DashboardGraph.Root>(navController)
        val featureComponent = component ?: getComponent(backStackEntry, rootEntry)
        return injectedViewModel(viewModelStoreOwner = rootEntry) {
            featureComponent.dashboardViewModel
        }
    }

    @Composable
    private fun getComponent(
        backStackEntry: NavBackStackEntry,
        rootEntry: NavBackStackEntry,
    ): DashboardComponent {
        val context = LocalContext.current
        val rootComponent = rootComponent(rootEntry, backStackEntry.arguments, context)
        return DaggerDashboardComponent.builder()
            .component(rootComponent)
            .dependencies((context as Activity).findDependencies())
            .build()
            .also { component = it }
    }
}
