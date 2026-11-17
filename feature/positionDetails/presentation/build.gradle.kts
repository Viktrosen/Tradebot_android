plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
}

android.namespace = moduleNamespace.versions.feature.positionDetails.presentation.get()

dependencies {
    testImplementation(testFixtures(project(":core:utils")))
    testImplementation(libs.junit)
    implementation(projects.feature.positionDetails.domain)
    implementation(projects.feature.positionDetails.router)
    implementation(projects.core.uikit)
    implementation(libs.bundles.orbit.mvi)
    implementation(libs.androidx.core.ktx)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.lifecycle.runtime.compose)
    debugImplementation(libs.compose.uiTooling)
    testImplementation(libs.jetbrains.coroutines.test)
}
