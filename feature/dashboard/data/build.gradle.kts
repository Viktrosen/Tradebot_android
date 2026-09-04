plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.dashboard.data.get()
dependencies {
    //Modules
    implementation(projects.feature.dashboard.domain)
    implementation(projects.feature.dashboard.api)
    implementation(projects.core.network)
    implementation(projects.core.utils)
    implementation(projects.service.network)

    // Libs
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.retrofit.converter.serialization)
    implementation(libs.firebase.messaging.ktx)
}