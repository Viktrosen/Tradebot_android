plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}
android.namespace = "ru.bolotov.feature.auth.api"
dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.jetbrains.serializationJson)
}
