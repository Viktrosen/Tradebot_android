plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.positionDetails.data.get()

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.jetbrains.coroutines.test)
    implementation(projects.feature.positionDetails.api)
    implementation(projects.feature.positionDetails.domain)
    implementation(projects.feature.positions.domain)
    implementation(projects.core.network)
    implementation(libs.jetbrains.coroutines.core)
}
