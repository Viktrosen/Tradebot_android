package ru.bolotov.beautyfinder.convention.configuration

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import ru.bolotov.beautyfinder.convention.utils.PluginAlias

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    pluginManager.apply {
        apply(PluginAlias.JETBRAINS_COMPOSE_COMPILER)
    }

    with(commonExtension) {
        defaultConfig {
            vectorDrawables {
                useSupportLibrary = true
            }
        }
        buildFeatures.compose = true
    }
}
