package ru.bolotov.beautyfinder.convention.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.bolotov.beautyfinder.convention.utils.LibAlias
import ru.bolotov.beautyfinder.convention.utils.ModuleAlias
import ru.bolotov.beautyfinder.convention.utils.PluginAlias
import ru.bolotov.beautyfinder.convention.utils.debugImplementation
import ru.bolotov.beautyfinder.convention.utils.implementation
import ru.bolotov.beautyfinder.convention.utils.implementationProject

class ComposeFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(PluginAlias.BOLOTOV_DAGGER)
            }

            dependencies {
                // Module
                implementationProject(ModuleAlias.DEPENDENCY)
                //implementationProject(ModuleAlias.DI)
                implementationProject(ModuleAlias.UTILS)
                implementationProject(ModuleAlias.COMMON_ROUTER)
                implementationProject(ModuleAlias.CORE_UIKIT)
                // Libs
                implementation(LibAlias.COMPOSE_ANIMATION)
                implementation(LibAlias.COMPOSE_FOUNDATION)
                implementation(LibAlias.COMPOSE_MATERIAL_3)
                implementation(LibAlias.COMPOSE_NAVIGATION)
                implementation(LibAlias.COMPOSE_RUNTIME)
                implementation(LibAlias.COMPOSE_RUNTIME_LIFECYCLE)
                implementation(LibAlias.COMPOSE_UI)
                implementation(LibAlias.COMPOSE_UI_TOOLING_PREVIEW)
                debugImplementation(LibAlias.COMPOSE_UI_TOOLING)
            }
        }
    }
}
