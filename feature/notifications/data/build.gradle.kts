plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.dagger)
}

android.namespace = moduleNamespace.versions.feature.notifications.data.get()

dependencies {
    implementation(projects.feature.notifications.api)
    implementation(projects.feature.notifications.domain)
}
