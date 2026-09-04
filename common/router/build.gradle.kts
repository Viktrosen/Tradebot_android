plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.compose.core)
}

android.namespace = moduleNamespace.versions.common.router.get()

dependencies {
    // Libs
    api(libs.navigation.ktx)
    implementation(libs.compose.navigation)
}