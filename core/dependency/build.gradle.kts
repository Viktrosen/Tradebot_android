plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.compose.core)
}

android.namespace = moduleNamespace.versions.core.dependency.get()

dependencies {
    // Module
    implementation(projects.common.router)
    // Libs
    implementation(libs.lifecycle.viewmodel.compose)
    api(libs.dagger)

}