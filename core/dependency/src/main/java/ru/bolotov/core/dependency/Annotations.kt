package ru.bolotov.core.dependency

import dagger.MapKey
import ru.bolotov.tradebot.router.FeatureEntry
import javax.inject.Scope
import kotlin.reflect.KClass

@MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)

@MapKey
annotation class FeatureEntryKey(val value: KClass<out FeatureEntry>)

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScoped

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SubFeatureScoped