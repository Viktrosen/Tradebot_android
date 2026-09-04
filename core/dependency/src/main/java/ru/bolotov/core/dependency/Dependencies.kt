package ru.bolotov.core.dependency

import ru.bolotov.tradebot.router.Destinations

interface Dependencies

typealias DependenciesMap = Map<Class<out Dependencies>, @JvmSuppressWildcards Dependencies>

interface HasDependencies {
    val dependenciesMap: DependenciesMap
    val dependenciesNav: Destinations
}