plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}

android.namespace = moduleNamespace.versions.feature.main.presentation.get()

dependencies {
    // Module
    //implementation(projects.feature.feed.router)
    //implementation(projects.feature.notifications.router)
    implementation(projects.feature.main.route)
    implementation(projects.core.uikit)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    implementation(projects.feature.dashboard.router)
    implementation(projects.feature.positions.router)
    implementation(projects.feature.instruments.router)
    implementation(projects.feature.strategy.router)
    implementation(projects.feature.risk.router)
    implementation(projects.feature.more.router)
    // Libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.orbit.mvi)
    implementation(libs.coil.compose)
}
