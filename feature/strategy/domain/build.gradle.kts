plugins { alias(libs.plugins.bolotov.lib) }
android.namespace = moduleNamespace.versions.feature.strategy.domain.get()
dependencies {
    testImplementation(libs.jetbrains.coroutines.core)
}
