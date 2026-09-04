plugins { alias(libs.plugins.bolotov.lib); alias(libs.plugins.bolotov.dagger) }
android.namespace = moduleNamespace.versions.feature.risk.data.get()
dependencies { implementation(projects.feature.risk.api); implementation(projects.feature.risk.domain) }
