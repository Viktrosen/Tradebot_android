plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}

android.namespace = moduleNamespace.versions.feature.splashScreen.presentation.get()


dependencies {
    // Module
    implementation(projects.core.dependency)

//    implementation(projects.common.entity)
//    implementation(projects.common.utils)
    implementation(projects.common.router)
    implementation(projects.core.uikit)
    implementation(projects.core.utils)

    implementation(projects.feature.splashScreen.router)
    implementation(projects.feature.main.route)
    // Libs
    implementation(libs.dagger)
    implementation(libs.bundles.orbit.mvi)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}