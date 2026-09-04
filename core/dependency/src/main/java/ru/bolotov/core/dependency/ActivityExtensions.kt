package ru.bolotov.core.dependency

import android.app.Activity
import ru.bolotov.tradebot.router.Destinations
import ru.bolotov.tradebot.router.FeatureEntry

internal val Activity.dependenciesApplication: HasDependencies?
    get() = application as? HasDependencies

//Dependencies
inline fun <reified D : Dependencies> Activity.findDependencies(): D {
    return findDependenciesByClass(D::class.java)
}

@Suppress("UNCHECKED_CAST")
fun <D : Dependencies> Activity.findDependenciesByClass(clazz: Class<D>): D {
    return dependenciesApplication?.dependenciesMap?.get(clazz) as? D
        ?: throw IllegalStateException("No Dependencies $clazz in application")
}


//Destinations
val Activity.destinationsProvider: Destinations
    get() = dependenciesApplication?.dependenciesNav
        ?: throw IllegalStateException("Nav graph destinations not found in application")

inline fun <reified K : FeatureEntry> Activity.findDestinations(): K {
    return findDestinationsByClass(K::class.java)
}

@Suppress("UNCHECKED_CAST")
fun <K : FeatureEntry> Activity.findDestinationsByClass(clazz: Class<K>): K {
    return dependenciesApplication?.dependenciesNav?.get(clazz) as? K
        ?: throw IllegalStateException("No Destination $clazz in application")
}
