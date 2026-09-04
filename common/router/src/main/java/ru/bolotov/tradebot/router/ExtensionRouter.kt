package ru.bolotov.tradebot.router

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

fun NavHostController.navigateAndClean(route: String) {
    this.getViewModelStoreOwner(this.graph.id).viewModelStore.clear()
    navigate(route = route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateLogout(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
            saveState = false
        }
    }
}

infix fun NavHostController.back(backStackEntry: NavBackStackEntry) {
    backStackEntry.withLifecycle { popBackStack() }
}

/**
 * Метод для корректной навигации на экран графа другой вкладки navigation bottom bar'a.
 * */
infix fun NavHostController.navigateToNestedRoute(route: String) {
    this.currentBackStackEntry?.withLifecycle {
        navigate(route) {
            graph.startDestinationRoute?.let { route ->
                popUpTo(route) { saveState = true }
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

infix fun NavHostController.navigateToTopStack(route: String) {
    navigate(route = route) {
        graph.startDestinationRoute?.let { route ->
            popUpTo(route) { saveState = true }
        }
        launchSingleTop = true
    }
}

fun NavHostController.navigateWithClearStack(
    route: Any,  // Изменяем с String на Any для поддержки типизированных route
    popUpToRoute: Any
) {
    this.navigate(route) {
        popUpTo(popUpToRoute) {
            inclusive = true
        }
    }
}
