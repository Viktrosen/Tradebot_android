package ru.bolotov.feature.risk.presentation.core

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
import ru.bolotov.feature.risk.presentation.di.DaggerRiskComponent
import ru.bolotov.feature.risk.presentation.di.DaggerRiskFeatureComponent
import ru.bolotov.feature.risk.presentation.di.RiskComponent
import ru.bolotov.feature.risk.presentation.di.RiskFeatureComponent
import ru.bolotov.feature.risk.presentation.ui.RiskScreen
import ru.bolotov.feature.risk.router.RiskEntry
import ru.bolotov.feature.risk.router.RiskGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class RiskEntryCore @Inject constructor() : RiskEntry(),
    RootComponentHolder<RiskFeatureComponent> {
    override val rootRoute: String
        get() = RiskGraph.Root::class.qualifiedName ?: "dashboard_root"

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): RiskFeatureComponent = rememberScoped(storeOwner = rootEntry) {
        DaggerRiskFeatureComponent.builder().build()
    }

    private var component: RiskComponent? = null

    override val deepLinks: List<NavDeepLink> = listOf()

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    ) {
        navigation<RiskGraph.Root>(startDestination = RiskGraph.Risk) {
            composable<RiskGraph.Risk> { backStackEntry ->
                RiskScreen(
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
    ): RiskViewModel {
        val rootEntry = backStackEntry.rememberBackStackEntry<RiskGraph.Root>(navController)
        val featureComponent = component ?: getComponent(backStackEntry, rootEntry)
        return injectedViewModel(viewModelStoreOwner = rootEntry) {
            featureComponent.riskViewModel
        }
    }

    @Composable
    private fun getComponent(
        backStackEntry: NavBackStackEntry,
        rootEntry: NavBackStackEntry,
    ): RiskComponent {
        val context = LocalContext.current
        val rootComponent = rootComponent(rootEntry, backStackEntry.arguments, context)
        return DaggerRiskComponent.builder()
            .component(rootComponent)
            .dependencies((context as Activity).findDependencies())
            .build()
            .also { component = it }
    }

}