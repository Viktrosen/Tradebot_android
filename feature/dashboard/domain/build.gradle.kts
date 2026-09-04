plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.feature.dashboard.domain.get()
dependencies {
    // Modules
    implementation(projects.core.utils)

    // Libs
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.retrofit.converter.serialization)
}