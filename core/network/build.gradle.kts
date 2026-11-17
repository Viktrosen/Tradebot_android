plugins {
    alias(libs.plugins.bolotov.lib)
}

android {
    namespace = moduleNamespace.versions.core.network.get()

    // Shrink only the final application: library shrinking removes public realtime
    // contracts before consuming modules (and their release tests) can reference them.
}

dependencies {
    testImplementation(libs.jetbrains.coroutines.test)
    implementation(libs.androidx.core.ktx)
    implementation(libs.okhttp)
    implementation(libs.jetbrains.serializationJson)
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.javax.inject)
}
