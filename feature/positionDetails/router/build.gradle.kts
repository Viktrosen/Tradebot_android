plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}

android.namespace = moduleNamespace.versions.feature.positionDetails.router.get()

dependencies {
    implementation(projects.common.router)
    implementation(libs.jetbrains.coroutines.core)
}
