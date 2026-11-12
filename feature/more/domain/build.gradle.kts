plugins {
    alias(libs.plugins.bolotov.lib)
}

android.namespace = moduleNamespace.versions.feature.more.domain.get()

dependencies {
    implementation(libs.jetbrains.coroutines.core)
}
