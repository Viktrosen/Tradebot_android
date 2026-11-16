plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.feature.positionDetails.domain.get()

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.jetbrains.coroutines.test)
    implementation(libs.jetbrains.coroutines.core)
}
