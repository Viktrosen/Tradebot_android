package ru.bolotov.feature.instruments.presentation.core

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
import ru.bolotov.feature.instruments.presentation.di.DaggerInstrumentsComponent
import ru.bolotov.feature.instruments.presentation.di.DaggerInstrumentsFeatureComponent
import ru.bolotov.feature.instruments.presentation.di.InstrumentsComponent
import ru.bolotov.feature.instruments.presentation.di.InstrumentsFeatureComponent
import ru.bolotov.feature.instruments.presentation.ui.InstrumentsScreen
import ru.bolotov.feature.instruments.router.InstrumentsEntry
import ru.bolotov.feature.instruments.router.InstrumentsGraph
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.RootComponentHolder
import ru.bolotov.tradebot.router.rememberBackStackEntry
import javax.inject.Inject

class InstrumentsEntryCore @Inject constructor() : InstrumentsEntry(), RootComponentHolder<InstrumentsFeatureComponent> {
    override val rootRoute: String get() = InstrumentsGraph.Root::class.qualifiedName ?: "instruments_root"
    private var component: InstrumentsComponent? = null
    override val deepLinks: List<NavDeepLink> = emptyList()
    @Composable override fun rootComponent(rootEntry: NavBackStackEntry, arguments: Bundle?, context: Context?) = rememberScoped(storeOwner = rootEntry) { DaggerInstrumentsFeatureComponent.builder().build() }
    override fun NavGraphBuilder.navigation(globalNavController: NavHostController, navController: NavHostController, destinations: Destinations) {
        navigation<InstrumentsGraph.Root>(startDestination = InstrumentsGraph.Instruments) {
            composable<InstrumentsGraph.Instruments> { entry -> InstrumentsScreen(viewModel(entry, navController)) }
        }
    }
    @Composable private fun viewModel(entry: NavBackStackEntry, navController: NavHostController): InstrumentsViewModel {
        val root = entry.rememberBackStackEntry<InstrumentsGraph.Root>(navController)
        val screenComponent = component ?: run {
            val featureComponent = rootComponent(root, entry.arguments, LocalContext.current)
            DaggerInstrumentsComponent.builder().component(featureComponent).dependencies((LocalContext.current as Activity).findDependencies()).build().also { component = it }
        }
        return injectedViewModel(viewModelStoreOwner = root) { screenComponent.instrumentsViewModel }
    }
}
