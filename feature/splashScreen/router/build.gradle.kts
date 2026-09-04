plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}

android.namespace = moduleNamespace.versions.feature.splashScreen.router.get()


dependencies {
    // Module
    implementation(projects.common.router)
    // Libs
    implementation(libs.navigation.ktx)
}