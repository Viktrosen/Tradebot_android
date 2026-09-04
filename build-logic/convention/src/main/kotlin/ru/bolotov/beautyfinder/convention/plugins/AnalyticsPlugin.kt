package ru.bolotov.beautyfinder.convention.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.bolotov.beautyfinder.convention.utils.LibAlias
import ru.bolotov.beautyfinder.convention.utils.ModuleAlias
import ru.bolotov.beautyfinder.convention.utils.implementation
import ru.bolotov.beautyfinder.convention.utils.implementationProject

class AnalyticsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                // Modules
                //implementationProject(ModuleAlias.COMMON_ANALYTICS)
                // Libs
                implementation(LibAlias.APPMETRICA)
                implementation(LibAlias.MIXPANEL)
                implementation(LibAlias.CUSTOMER_IO)
            }
        }
    }
}
