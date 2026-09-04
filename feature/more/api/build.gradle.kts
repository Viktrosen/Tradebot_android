plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.more.api.get()

dependencies {
    implementation(projects.core.network)
    implementation(libs.retrofit.converter.serialization)
}
