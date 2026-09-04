plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.jetbrains.kotlinGradle)
}

gradlePlugin {
    plugins {
        register("libModulePlugin") {
            id = "bolotov.libs"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.LibModulePlugin"
        }
        register("appModulePlugin") {
            id = "bolotov.app"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.AppModulePlugin"
        }
        register("composeCorePlugin") {
            id = "bolotov.compose.core"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.ComposeCorePlugin"
        }
        register("composeFeaturePlugin") {
            id = "bolotov.compose.feature"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.ComposeFeaturePlugin"
        }
        register("daggerPlugin") {
            id = "bolotov.dagger"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.DaggerPlugin"
        }
        register("analyticsPlugin") {
            id = "bolotov.analytics"
            implementationClass = "ru.bolotov.beautyfinder.convention.plugins.AnalyticsPlugin"
        }
    }
}