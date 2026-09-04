plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}

android.namespace = moduleNamespace.versions.feature.instruments.presentation.get()

dependencies {
    implementation(projects.feature.instruments.router)
    implementation(projects.feature.instruments.domain)
    implementation(projects.core.network)
    implementation(projects.core.uikit)
    implementation(libs.bundles.orbit.mvi)
}
