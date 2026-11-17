plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.feature.dashboard.domain.get()
dependencies {
    testImplementation(libs.jetbrains.coroutines.test)
    // Modules
    implementation(projects.core.utils)

    // Libs
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.retrofit.converter.serialization)
}
