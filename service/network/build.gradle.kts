plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.bolotov.compose.core)
    alias(libs.plugins.bolotov.compose.feature)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}

android.namespace = moduleNamespace.versions.service.network.get()

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.utils)

    implementation(libs.androidx.core.ktx)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.serialization)
    implementation(libs.jetbrains.serializationJson)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)
}