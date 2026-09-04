plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}

android.namespace = moduleNamespace.versions.feature.dashboard.presentation.get()

dependencies {
    // Module
    //implementation(projects.feature.feed.router)
    //implementation(projects.feature.notifications.router)
    implementation(projects.feature.dashboard.domain)
    implementation(projects.feature.risk.domain)
    implementation(projects.core.network)
    implementation(projects.feature.dashboard.router)
    implementation(projects.feature.instruments.router)
    implementation(projects.core.uikit)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
//    implementation(projects.feature.profile.router)
//    implementation(projects.feature.map.router)
//    implementation(projects.feature.scheduler.router)
//    implementation(projects.feature.chats.router)
    // Libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.orbit.mvi)
    implementation(libs.coil.compose)
}
