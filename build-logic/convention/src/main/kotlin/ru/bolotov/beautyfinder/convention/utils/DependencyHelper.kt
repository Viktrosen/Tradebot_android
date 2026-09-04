package ru.bolotov.beautyfinder.convention.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project

private const val IMPLEMENTATION = "implementation"
private const val DEBUG_IMPLEMENTATION = "debugImplementation"
private const val KAPT = "kapt"

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.implementation(dependencyNotation: String) {
    dependencies.add(IMPLEMENTATION, libs.findLibrary(dependencyNotation).get())
}

internal fun Project.debugImplementation(dependencyNotation: String) {
    dependencies.add(DEBUG_IMPLEMENTATION, libs.findLibrary(dependencyNotation).get())
}

internal fun Project.implementationProject(alias: String) = with(dependencies) {
    add(IMPLEMENTATION, project(alias))
}

internal fun Project.kapt(dependencyNotation: String) {
    dependencies.add(KAPT, libs.findLibrary(dependencyNotation).get())
}
