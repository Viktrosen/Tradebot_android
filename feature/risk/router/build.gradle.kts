plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}

android.namespace = moduleNamespace.versions.feature.risk.router.get()

dependencies {
    implementation(projects.common.router)
}