package ru.bolotov.feature.main.presentation.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import ru.bolotov.feature.main.presentation.ui.util.reducedRoute
import ru.bolotov.core.dependency.destinationsProvider
import ru.bolotov.core.dependency.findDestinations
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.uikit.components.toast
import ru.bolotov.feature.dashboard.router.DashboardEntry
import ru.bolotov.feature.dashboard.router.DashboardGraph
import ru.bolotov.feature.instruments.router.InstrumentsEntry
import ru.bolotov.feature.instruments.router.InstrumentsGraph
import ru.bolotov.feature.main.presentation.core.Action
import ru.bolotov.feature.main.presentation.core.UiState
import ru.bolotov.feature.main.presentation.navigation.BottomBarItem
import ru.bolotov.feature.main.presentation.navigation.BottomBarTag
import ru.bolotov.feature.main.presentation.navigation.NavigationBottomBar
import ru.bolotov.feature.more.router.MoreEntry
import ru.bolotov.feature.more.router.MoreGraph
import ru.bolotov.feature.positions.router.PositionsEntry
import ru.bolotov.feature.positions.router.PositionsGraph
import ru.bolotov.feature.risk.router.RiskEntry
import ru.bolotov.feature.risk.router.RiskGraph
import ru.bolotov.feature.strategy.router.StrategyEntry
import ru.bolotov.feature.strategy.router.StrategyGraph
import ru.bolotov.tradebot.core.uikit.R
import ru.bolotov.tradebot.router.navigateWithClearStack

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun MainScreenHost(
    state: UiState,
    activity: Activity,
    globalNavController: NavHostController,
    action: (Action) -> Unit,
) {
    val navController = rememberNavController()

    val dashboardEntry = activity.findDestinations<DashboardEntry>()
    val positionsEntry = activity.findDestinations<PositionsEntry>()
    val instrumentsEntry = activity.findDestinations<InstrumentsEntry>()
    val strategyEntry = activity.findDestinations<StrategyEntry>()
    val riskEntry = activity.findDestinations<RiskEntry>()
    val moreEntry = activity.findDestinations<MoreEntry>()

    val invisibleBottomBarEntries = listOf(
        InstrumentsGraph.Instruments::class.qualifiedName.orEmpty()
    )

    val bottomItems by remember {
        derivedStateOf {
            listOf(
                BottomBarItem(
                    route = dashboardEntry.featureRoute,
                    icon = R.drawable.ic_map,
                    text = R.string.dashboard,
                    tag = BottomBarTag.Dashboard,
                    graphRoute = DashboardGraph.Root  // Добавляем типизированный route
                ),
                BottomBarItem(
                    route = positionsEntry.featureRoute,
                    icon = R.drawable.ic_calendar,
                    text = R.string.positions,
                    tag = BottomBarTag.Positions,
                    graphRoute = PositionsGraph.Root  // Добавляем типизированный route
                ),
                BottomBarItem(
                    route = strategyEntry.featureRoute,
                    icon = R.drawable.ic_chat,
                    text = R.string.strategy,
                    tag = BottomBarTag.Strategy,
                    graphRoute = StrategyGraph.Root  // Добавляем типизированный route
                ),
                BottomBarItem(
                    route = riskEntry.featureRoute,
                    icon = R.drawable.ic_profile,
                    text = R.string.risk,
                    tag = BottomBarTag.Risk,
                    graphRoute = RiskGraph.Root  // Добавляем типизированный route
                ),
                BottomBarItem(
                    route = moreEntry.featureRoute,
                    icon = R.drawable.ic_profile,
                    text = R.string.more,
                    tag = BottomBarTag.More,
                    graphRoute = MoreGraph.Root  // Добавляем типизированный route
                )
            )
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        val destination = bottomItems.find {
            it.reducedRoute == currentDestination?.reducedRoute?.split(".")?.lastOrNull()?.trim()?.lowercase()
        }
        if (destination != null) {
            action(Action.UpdateCurrentSection(destination.graphRoute))
        }
    }

    BackDoublePressExit(
        bottomItemsFirstItemRoute = bottomItems.firstOrNull()?.route,
        currentSection = state.currentSection,
        currentDestination = currentDestination,
        mainEntryRoute = dashboardEntry.featureRoute,
        navController = navController
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = TradebotTheme.colors.background.Base,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBottomBar(
                routes = bottomItems,
                invisibleBottomBarEntries = invisibleBottomBarEntries,
                navController = navController,
                currentSection = state.currentSection,
                updateCurrentSection = { route ->
                    action(Action.UpdateCurrentSection(route))
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            startDestination = DashboardGraph.Root,
            navController = navController
        ) {
            with(dashboardEntry) {
                navigation(
                    globalNavController = globalNavController,
                    navController = navController,
                    destinations = activity.destinationsProvider
                )
            }

            with(positionsEntry) {
                navigation(
                    globalNavController = globalNavController,
                    navController = navController,
                    destinations = activity.destinationsProvider
                )
            }

            with(instrumentsEntry) {
                navigation(globalNavController = globalNavController, navController = navController, destinations = activity.destinationsProvider)
            }

            with(strategyEntry) {
                navigation(
                    globalNavController = globalNavController,
                    navController = navController,
                    destinations = activity.destinationsProvider
                )
            }

            with(riskEntry) {
                navigation(
                    globalNavController = globalNavController,
                    navController = navController,
                    destinations = activity.destinationsProvider
                )
            }

            with(moreEntry) {
                navigation(
                    globalNavController = globalNavController,
                    navController = navController,
                    destinations = activity.destinationsProvider
                )
            }
        }
    }
}

private const val EXIT_TIME_MILLS = 2000L

@Composable
private fun BackDoublePressExit(
    navController: NavHostController,
    currentDestination: NavDestination?,
    bottomItemsFirstItemRoute: String?,
    currentSection: Any?,
    mainEntryRoute: String
) {
    var exit by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(key1 = exit) {
        if (exit) {
            delay(EXIT_TIME_MILLS)
            exit = false
        }
    }

    BackHandler(enabled = true) {
        if (currentDestination?.route?.split("/")?.firstOrNull() != bottomItemsFirstItemRoute) {
            navController.navigateWithClearStack(
                currentSection ?: mainEntryRoute,
                currentSection ?: mainEntryRoute
            )
        } else if (exit) {
            finishAffinity(context as Activity)
        } else {
            exit = true
            context.toast(context.getString(R.string.common_press_again_message))
        }
    }
}
