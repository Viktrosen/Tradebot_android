package ru.bolotov.feature.main.presentation.ui.util

import androidx.navigation.NavDestination
import ru.bolotov.feature.main.presentation.navigation.BottomBarItem

val NavDestination.reducedRoute
    get() = this.route?.substringBefore("?")?.trim()


val BottomBarItem.reducedRoute
    get() = this.route.substringBefore("?").trim()