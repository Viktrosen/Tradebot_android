plugins { alias(libs.plugins.bolotov.lib); alias(libs.plugins.bolotov.dagger) }
android.namespace = moduleNamespace.versions.feature.strategy.data.get()
dependencies { implementation(projects.feature.strategy.api); implementation(projects.feature.strategy.domain) }
