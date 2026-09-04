plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.instruments.data.get()

dependencies {
    implementation(projects.feature.instruments.api)
    implementation(projects.feature.instruments.domain)
}
