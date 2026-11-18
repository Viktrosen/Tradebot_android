plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.core.utils.get()

dependencies {
    // Libs
    implementation(platform(libs.firebase.bom))
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.firebase.analytic)
}
