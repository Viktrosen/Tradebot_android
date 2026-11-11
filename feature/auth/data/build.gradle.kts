plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}
android.namespace = "ru.bolotov.feature.auth.data"
dependencies {
    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.domain)
    implementation(libs.okhttp)
    implementation(libs.retrofit.core)
    implementation(libs.jetbrains.serializationJson)
    implementation(libs.jetbrains.coroutines.core)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation(libs.junit)
    testImplementation(libs.retrofit.converter.serialization)
}
