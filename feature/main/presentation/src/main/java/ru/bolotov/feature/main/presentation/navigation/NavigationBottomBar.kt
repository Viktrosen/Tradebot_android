package ru.bolotov.feature.main.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.bolotov.feature.main.presentation.ui.util.reducedRoute
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.tradebot.router.navigateWithClearStack

@Composable
internal fun NavigationBottomBar(
    routes: List<BottomBarItem>,
    invisibleBottomBarEntries: List<String>,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    currentSection: Any?,
    updateCurrentSection: (Any?) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (routes.size !in 2..5)
        throw IllegalArgumentException("routers size ${routes.size} but expected from 2 to 5 )")

    AnimatedVisibility(!invisibleBottomBarEntries.contains(currentDestination?.reducedRoute)) {
        NavigationBar(
            modifier = modifier.border(
                width = .5.dp,
                color = TradebotTheme.colors.gray.Gray60,
            ),
            containerColor = TradebotTheme.colors.base.White,
        ) {
            routes.forEach { screen ->
                key(screen) {
                    val selected = screen.graphRoute == currentSection
                    NavigationBarItem(
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(screen.icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (selected) {
                                    TradebotTheme.colors.icon.Selected
                                } else {
                                    TradebotTheme.colors.icon.Unselected
                                }
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(screen.text),
                                style = TradebotTheme.typography.helperMed,
                                color = if (selected) {
                                    TradebotTheme.colors.icon.Selected
                                } else {
                                    TradebotTheme.colors.icon.Unselected
                                }
                            )
                        },
                        colors = NavigationBarItemColors(
                            selectedIconColor = Color.Unspecified,
                            selectedTextColor = Color.Unspecified,
                            selectedIndicatorColor = TradebotTheme.colors.base.White90,
                            unselectedIconColor = Color.Unspecified,
                            unselectedTextColor = Color.Unspecified,
                            disabledIconColor = Color.Unspecified,
                            disabledTextColor = Color.Unspecified
                        ),
                        onClick = {
                            updateCurrentSection(screen.graphRoute)
                            if (!selected) {
                                // Используем типизированный route, если он есть
                                val targetRoute = screen.graphRoute ?: screen.route
                                navController.navigate(targetRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                // Для повторного клика используем clear stack
                                val targetRoute = screen.graphRoute ?: screen.route
                                navController.navigateWithClearStack(
                                    route = targetRoute,
                                    popUpToRoute = targetRoute
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}