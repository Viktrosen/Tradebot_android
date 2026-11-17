plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.core.utils.get()
android.testFixtures.enable = true

dependencies {
    testFixturesImplementation(libs.jetbrains.coroutines.test)
    testFixturesImplementation(libs.orbit.core)
    // Libs
    implementation(platform(libs.firebase.bom))
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.firebase.analytic)
}
