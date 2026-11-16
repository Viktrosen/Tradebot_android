package ru.bolotov.tradebot.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.StateFlow
import ru.bolotov.feature.auth.router.AuthGraph
import ru.bolotov.feature.main.route.MainGraph
import ru.bolotov.tradebot.navigation.data.AppNavigationState
import ru.bolotov.tradebot.navigation.data.requiresNavigation
import ru.bolotov.tradebot.navigation.data.toMainRoute

/**
 * Owns root Auth/Main transitions and external-link handoff. State stays current in the ViewModel,
 * but navigation runs only while STARTED.
 */
@Composable
internal fun AppNavigationHost(
    activity: ComponentActivity,
    navigationState: StateFlow<AppNavigationState>,
    navigationInstanceId: String,
    onLinkHandled: (String) -> Unit,
) {
    key(navigationInstanceId) {
        val navController = rememberNavController()
        var displayedSessionId by rememberSaveable { mutableStateOf<String?>(null) }
        val currentOnLinkHandled by rememberUpdatedState(newValue = onLinkHandled)

        Navigation(activity = activity, globalNavController = navController)

        LaunchedEffect(key1 = activity, key2 = navigationState, key3 = navController) {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigationState.collect { state ->
                    val current = navController.currentDestination
                    if (
                        state.requiresNavigation(
                            isAuthDestination = current?.hasRoute<AuthGraph.Root>() == true,
                            isMainDestination = current?.hasRoute<MainGraph.Root>() == true,
                            displayedSessionId = displayedSessionId,
                        )
                    ) {
                        val target =
                            when (state) {
                                AppNavigationState.Auth -> AuthGraph.Root
                                is AppNavigationState.Main ->
                                    state.pendingLink?.toMainRoute() ?: MainGraph.Root()
                            }
                        navController.navigate(target) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                            // A new session must not reuse protected screens or ViewModels.
                            restoreState = false
                        }
                    }
                    displayedSessionId = (state as? AppNavigationState.Main)?.sessionId
                    (state as? AppNavigationState.Main)?.pendingLink?.let { request ->
                        currentOnLinkHandled(request.requestId)
                    }
                }
            }
        }
    }
}
