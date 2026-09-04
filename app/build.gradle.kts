plugins {
    alias(libs.plugins.bolotov.app)
    alias(libs.plugins.bolotov.dagger)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.jetbrains.kotlinSerialization)
}

android.namespace = moduleNamespace.versions.app.get()

dependencies {
// Module
    implementation(projects.common.router)
    implementation(projects.core.uikit)

    implementation(projects.core.utils)
    implementation(projects.core.dependency)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.common.router)

    implementation(projects.feature.splashScreen.presentation)
    implementation(projects.feature.splashScreen.router)

    implementation(projects.feature.main.presentation)
    implementation(projects.feature.main.route)

    implementation(projects.feature.dashboard.domain)
    implementation(projects.feature.dashboard.presentation)
    implementation(projects.feature.dashboard.router)
    implementation(projects.feature.dashboard.data)
    implementation(projects.feature.dashboard.api)

    implementation(projects.feature.positions.presentation)
    implementation(projects.feature.positions.router)
    implementation(projects.feature.positions.domain)
    implementation(projects.feature.positions.data)
    implementation(projects.feature.positions.api)

    implementation(projects.feature.instruments.presentation)
    implementation(projects.feature.instruments.router)
    implementation(projects.feature.instruments.domain)
    implementation(projects.feature.instruments.data)
    implementation(projects.feature.instruments.api)

    implementation(projects.feature.strategy.presentation)
    implementation(projects.feature.strategy.router)
    implementation(projects.feature.strategy.domain)
    implementation(projects.feature.strategy.data)
    implementation(projects.feature.strategy.api)

    implementation(projects.feature.notifications.api)
    implementation(projects.feature.notifications.domain)
    implementation(projects.feature.notifications.data)

    implementation(projects.feature.risk.presentation)
    implementation(projects.feature.risk.router)

    implementation(projects.feature.more.presentation)
    implementation(projects.feature.more.router)
    implementation(projects.feature.more.api)
    implementation(projects.feature.more.domain)
    implementation(projects.feature.more.data)
    implementation(projects.feature.risk.api)
    implementation(projects.feature.risk.domain)
    implementation(projects.feature.risk.data)

    implementation(projects.service.network)

    //Libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.splashscreen)
    implementation(libs.compose.activity)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.networkOkHttp)
    implementation(libs.jetbrains.serializationJson)
    implementation(libs.paging.compose)
    implementation(platform(libs.compose.bom))

    implementation(libs.androidx.security.crypto)

    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.firebase.messaging.ktx)
    debugImplementation(libs.compose.uiTooling)
    implementation(platform(libs.firebase.bom))
    //implementation(libs.firebase.crashlytics)
    //implementation(libs.firebase.analytics)

    // Test
    debugImplementation(libs.chucker)
    debugImplementation(libs.leakcanary)
    releaseImplementation(libs.chucker.noop)
}
