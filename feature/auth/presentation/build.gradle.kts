plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}
android.namespace = "ru.bolotov.feature.auth.presentation"
dependencies {
    implementation(projects.feature.auth.domain)
    implementation(projects.feature.auth.router)
    implementation(libs.bundles.orbit.mvi)
}
