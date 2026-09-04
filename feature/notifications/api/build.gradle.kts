plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.notifications.api.get()

dependencies {
    implementation(projects.core.network)
    implementation(libs.retrofit.converter.serialization)
}
