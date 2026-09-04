package ru.bolotov.features.more.presentation.core

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
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.core.dependency.rememberScoped
import androidx.navigation.compose.navigation
import ru.bolotov.features.more.presentation.di.MoreComponent
import ru.bolotov.features.more.presentation.di.MoreFeatureComponent
import ru.bolotov.feature.more.router.MoreEntry
import ru.bolotov.feature.more.router.MoreGraph
import ru.bolotov.features.more.presentation.di.DaggerMoreComponent
import ru.bolotov.features.more.presentation.di.DaggerMoreFeatureComponent
import ru.bolotov.features.more.presentation.ui.MoreScreen
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class MoreEntryCore @Inject constructor() : MoreEntry(),
    RootComponentHolder<MoreFeatureComponent> {
    override val rootRoute: String
        get() = MoreGraph.Root::class.qualifiedName ?: "dashboard_root"

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): MoreFeatureComponent = rememberScoped(storeOwner = rootEntry) {
        DaggerMoreFeatureComponent.builder().build()
    }

    private var component: MoreComponent? = null

    override val deepLinks: List<NavDeepLink> = listOf()

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    ) {
        navigation<MoreGraph.Root>(startDestination = MoreGraph.More) {
            composable<MoreGraph.More> { backStackEntry ->
                MoreScreen(
                    viewModel = getMoreViewModel(
                        backStackEntry = backStackEntry,
                        navController = navController
                    )
                )
            }
        }
    }

    @Composable
    private fun getMoreViewModel(
        backStackEntry: NavBackStackEntry,
        navController: NavHostController
    ): MoreViewModel {
        val rootEntry = backStackEntry.rememberBackStackEntry<MoreGraph.Root>(navController)
        val featureComponent = component ?: getComponent(backStackEntry, rootEntry)
        return injectedViewModel(viewModelStoreOwner = rootEntry) {
            featureComponent.moreViewModel
        }
    }

    @Composable
    private fun getComponent(
        backStackEntry: NavBackStackEntry,
        rootEntry: NavBackStackEntry,
    ): MoreComponent {
        val context = LocalContext.current
        val rootComponent = rootComponent(rootEntry, backStackEntry.arguments, context)
        return DaggerMoreComponent.builder()
            .component(rootComponent)
            .dependencies((context as Activity).findDependencies())
            .build()
            .also { component = it }
    }

}