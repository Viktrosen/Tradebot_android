plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.dashboard.api.get()
dependencies {
    // Modules
    implementation(projects.core.network)

    // Libs
    implementation(libs.retrofit.converter.serialization)
}