package ru.bolotov.feature.positionDetails.presentation.core

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import javax.inject.Inject
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.core.dependency.rememberScoped
import ru.bolotov.feature.positionDetails.presentation.di.DaggerPositionDetailsComponent
import ru.bolotov.feature.positionDetails.presentation.di.DaggerPositionDetailsFeatureComponent
import ru.bolotov.feature.positionDetails.presentation.di.PositionDetailsDependencies
import ru.bolotov.feature.positionDetails.presentation.di.PositionDetailsFeatureComponent
import ru.bolotov.feature.positionDetails.presentation.ui.PositionDetailsScreen
import ru.bolotov.feature.positionDetails.router.PositionDetailsEntry
import ru.bolotov.feature.positionDetails.router.PositionDetailsGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry

class PositionDetailsEntryCore @Inject constructor() :
    PositionDetailsEntry(), RootComponentHolder<PositionDetailsFeatureComponent> {
    override val rootRoute: String
        get() = requireNotNull(PositionDetailsGraph.Root::class.qualifiedName)

    override val deepLinks: List<NavDeepLink> = emptyList()

    @Composable
    override fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?,
    ): PositionDetailsFeatureComponent =
        rememberScoped(storeOwner = rootEntry) { DaggerPositionDetailsFeatureComponent.create() }

    override fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations,
    ) {
        navigation<PositionDetailsGraph.Root>(startDestination = PositionDetailsGraph.Details) {
            composable<PositionDetailsGraph.Details> { entry ->
                val rootEntry =
                    entry.rememberBackStackEntry<PositionDetailsGraph.Root>(navController)
                val context = androidx.compose.ui.platform.LocalContext.current
                val feature =
                    rootComponent(
                        rootEntry = rootEntry,
                        arguments = entry.arguments,
                        context = context,
                    )
                val component =
                    rememberScoped(storeOwner = entry) {
                        DaggerPositionDetailsComponent.builder()
                            .feature(feature)
                            .dependencies(
                                (context as Activity).findDependencies<
                                    PositionDetailsDependencies
                                >()
                            )
                            .positionId(rootEntry.toRoute<PositionDetailsGraph.Root>().positionId)
                            .savedStateHandle(entry.savedStateHandle)
                            .build()
                    }
                PositionDetailsScreen(
                    viewModel =
                        injectedViewModel(viewModelStoreOwner = entry) { component.viewModel },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
