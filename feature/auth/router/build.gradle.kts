plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}
android.namespace = "ru.bolotov.feature.auth.router"
dependencies {
    implementation(projects.common.router)
    implementation(libs.navigation.ktx)
}
