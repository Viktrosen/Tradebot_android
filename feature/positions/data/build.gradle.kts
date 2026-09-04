plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.positions.data.get()

dependencies {
    implementation(projects.feature.positions.api)
    implementation(projects.feature.positions.domain)
    implementation(libs.retrofit.core)
}
