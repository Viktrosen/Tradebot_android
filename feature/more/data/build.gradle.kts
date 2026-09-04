plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.more.data.get()

dependencies {
    implementation(projects.feature.more.api)
    implementation(projects.feature.more.domain)
}
