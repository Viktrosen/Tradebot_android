package ru.bolotov.beautyfinder.convention.configuration

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.bolotov.beautyfinder.convention.utils.LibAlias
import ru.bolotov.beautyfinder.convention.utils.PluginAlias
import ru.bolotov.beautyfinder.convention.utils.implementation
import ru.bolotov.beautyfinder.convention.utils.kapt

internal fun Project.configureDagger() {
    pluginManager.apply {
        apply(PluginAlias.KOTLIN_KAPT)
    }

    dependencies {
        implementation(LibAlias.DAGGER)
        kapt(LibAlias.DAGGER_COMPILER)
    }
}
