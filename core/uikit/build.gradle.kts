plugins {
    alias(libs.plugins.bolotov.lib)
    alias(libs.plugins.bolotov.compose.core)
}

android.namespace = moduleNamespace.versions.core.ui.kit.get()

dependencies {
    // Libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.foundation.layout.android)

    implementation(libs.compose.ui)
    implementation(libs.compose.activity)
    implementation(libs.compose.material3)
    /*implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)*/
    //Coil
    implementation(libs.coil.compose)

    //paging
    implementation(libs.paging.common)
    implementation(libs.paging.compose)
}