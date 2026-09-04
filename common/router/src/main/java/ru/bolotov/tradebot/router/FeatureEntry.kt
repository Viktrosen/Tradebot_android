package ru.bolotov.tradebot.router

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

typealias Destinations = Map<Class<out FeatureEntry>, @JvmSuppressWildcards FeatureEntry>

interface FeatureEntry {

    val featureRoute: String
        get() = this::class.java.name  // Автоматически используем имя класса как route

    val deepLinks: List<NavDeepLink>
        get() = emptyList()
}

interface ComposableFeatureEntry : FeatureEntry {

    fun NavGraphBuilder.animatedComposable(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations,
        enterTransition: (() -> EnterTransition?)? = null,
        exitTransition: (() -> ExitTransition?)? = null,
        popEnterTransition: (() -> EnterTransition?)? = null,
        popExitTransition: (() -> ExitTransition?)? = null
    )
}

interface AggregateFeatureEntry : FeatureEntry {
    fun NavGraphBuilder.navigation(
        globalNavController: NavHostController,
        navController: NavHostController,
        destinations: Destinations
    )
}

inline fun <reified T : FeatureEntry> Destinations.find(): T =
    findOrNull() ?: error("Unable to find '${T::class.java}' destination.")

inline fun <reified T : FeatureEntry> Destinations.findOrNull(): T? =
    this[T::class.java] as? T

@Composable
fun NavBackStackEntry.rememberBackStackEntry(
    navController: NavHostController,
    route: String
): NavBackStackEntry {
    return remember(this) { navController.getBackStackEntry(route) }
}

@Composable
inline fun <reified T : Any> NavBackStackEntry.rememberBackStackEntry(
    navController: NavHostController
): NavBackStackEntry {
    return remember(this) { navController.getBackStackEntry<T>() }
}