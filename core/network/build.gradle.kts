plugins {
    alias(libs.plugins.bolotov.lib)
}

android {
    namespace = moduleNamespace.versions.core.network.get()

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.okhttp)
    implementation(libs.jetbrains.serializationJson)
    implementation(libs.jetbrains.coroutines.core)
    implementation(libs.javax.inject)
}
