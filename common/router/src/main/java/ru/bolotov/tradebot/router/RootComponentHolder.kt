package ru.bolotov.tradebot.router

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

interface RootComponentHolder<C : Any> {

    val rootRoute: String

    @Composable
    fun rootComponent(
        currentEntry: NavBackStackEntry,
        navController: NavHostController,
        arguments: Bundle?
    ): C {
        val rootEntry = currentEntry.rememberBackStackEntry(navController, rootRoute)
        return rootComponent(rootEntry, arguments, null)
    }

    @Composable
    fun rootComponent(
        rootEntry: NavBackStackEntry,
        arguments: Bundle?,
        context: Context?
    ): C
}