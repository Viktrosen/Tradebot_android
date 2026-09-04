package ru.bolotov.beautyfinder.convention.plugins

import com.android.build.api.dsl.VariantDimension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import ru.bolotov.beautyfinder.convention.configuration.FlavorDimension
import ru.bolotov.beautyfinder.convention.configuration.configureCommon
import ru.bolotov.beautyfinder.convention.configuration.configureCompose
import ru.bolotov.beautyfinder.convention.configuration.configureDagger
import ru.bolotov.beautyfinder.convention.utils.PluginAlias
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class AppModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            this.applyPlugins()
            extensions.configure<BaseAppModuleExtension> {
                configureCommon(this)
                configureCompose(this)
                configureDagger()
                applyFlavors(this@with)
                applySigningConfigs(this@with)
                applyBuildTypes()
                applyOutputFileName(target)
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }
        }
    }

    private fun Project.applyPlugins() = with(pluginManager) {
        apply(PluginAlias.ANDROID_APPLICATION)
        apply(PluginAlias.KOTLIN_ANDROID)
        apply(PluginAlias.JETBRAINS_COMPOSE_COMPILER)
//        apply(PluginAlias.GOOGLE_SERVICES)
    }

    private fun BaseAppModuleExtension.applySigningConfigs(project: Project) = this.signingConfigs {

        val keystoreFileName = "${project.projectDir.absolutePath}/config/keystore.properties"
        val keystoreProperties = Properties().apply {
            load(FileInputStream(project.file(keystoreFileName)))
        }

        create("release") {
            with(keystoreProperties) {
                storeFile = project.file(getProperty("storeFile"))
                storePassword = getProperty("storePassword")
                keyAlias = getProperty("keyAlias")
                keyPassword = getProperty("keyPassword")
            }
        }
    }

    private fun BaseAppModuleExtension.applyOutputFileName(project: Project) =
        applicationVariants.all {
            val flavors = this.productFlavors
            val appName = Config.FILE_NAME
            val appVersion = versionName
            val buildTypeName = buildType.name
            val backend = flavors.find { it.dimension == "backend" }?.name ?: ""
            val store = flavors.find { it.dimension == "store" }?.name ?: ""
            val fileName = "${appName}_$appVersion-$backend-$store-$buildTypeName"
            outputs.all {
                if (this is BaseVariantOutputImpl) {
                    outputFileName = "$fileName.apk"
                }
            }

            project.tasks.named(
                "sign${flavorName.uppercaseFirstChar()}${buildType.name.uppercaseFirstChar()}Bundle",
                FinalizeBundleTask::class.java
            ) {
                val file = finalBundleFile.asFile.get()
                val finalFile =
                    File(
                        file.parentFile,
                        "$fileName.aab"
                    )
                finalBundleFile.set(finalFile)
            }
        }

    private fun BaseAppModuleExtension.applyFlavors(project: Project) {
        flavorDimensions += listOf(
            FlavorDimension.Backend.name,
        )

        this.productFlavors {

            defaultConfig {
                buildFeatures.buildConfig = true
                applicationId = Config.APPLICATION_ID
                targetSdk = Config.TARGET_SDK
                versionCode = Config.VERSION_CODE
                versionName = Config.versionName
                manifestPlaceholders[APP_LABEL_MANIFEST_PLACEHOLDER_KEY] = "@string/app_name"
                manifestPlaceholders[APP_FLAVOR_MANIFEST_PLACEHOLDER_KEY] = ""
                ndk {
                    abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
                }
            }

            create(FlavorDimension.Backend.DEV) {
                dimension = FlavorDimension.Backend.name
                //applicationIdSuffix = Config.Dev.APPLICATION_ID_SUFFIX
                manifestPlaceholders.apply {
                    put(APP_FLAVOR_MANIFEST_PLACEHOLDER_KEY, "_dev")
                    put(APP_ICON_MANIFEST_PLACEHOLDER_KEY, "${APP_ICON_PREFIX}ic_launcher_dev")
                    put(
                        APP_BACKEND_URL_MANIFEST_PLACEHOLDER_KEY,
                        Config.Dev.BACKEND_URL
                            .substring(9 until Config.Dev.BACKEND_URL.length - 1)
                    )
                }
                setBuildConfigField(
                    field = FlavorsField.BACKEND_URL,
                    value = Config.Dev.BACKEND_URL
                )
                setBuildConfigField(
                    field = FlavorsField.MAPKIT_API_KEY,
                    getMapApiKey(project)
                )
            }

            create(FlavorDimension.Backend.PRODUCTION) {
                dimension = FlavorDimension.Backend.name
                manifestPlaceholders.apply {
                    put(APP_FLAVOR_MANIFEST_PLACEHOLDER_KEY, "")
                    put(APP_ICON_MANIFEST_PLACEHOLDER_KEY, "${APP_ICON_PREFIX}ic_launcher")
                    put(
                        APP_BACKEND_URL_MANIFEST_PLACEHOLDER_KEY,
                        Config.Prod.BACKEND_URL
                            .substring(9 until Config.Prod.BACKEND_URL.length - 1)
                    )
                }
                setBuildConfigField(
                    field = FlavorsField.BACKEND_URL,
                    value = Config.Prod.BACKEND_URL
                )
                setBuildConfigField(
                    field = FlavorsField.MAPKIT_API_KEY,
                    getMapApiKey(project)
                )
            }
        }
    }

    private fun getMapApiKey(project: Project): String {
        val secretFileName = "${project.projectDir.absolutePath}/secrets.properties"
        val secretProperties = Properties().apply {
            load(FileInputStream(project.file(secretFileName)))
        }

        return secretProperties.getProperty("MAPKIT_API_KEY")
    }

    private fun VariantDimension.setBuildConfigField(field: Pair<String, String>, value: String) {
        buildConfigField(
            type = field.first,
            name = field.second,
            value = value,
        )
    }

    private fun BaseAppModuleExtension.applyBuildTypes() = this.buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ""
            manifestPlaceholders[APP_CONFIG_MANIFEST_PLACEHOLDER_KEY] = "_debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders[APP_CONFIG_MANIFEST_PLACEHOLDER_KEY] = ""
        }
    }

    companion object {

        private const val APP_ICON_PREFIX = "@mipmap/"
        private const val APP_ICON_MANIFEST_PLACEHOLDER_KEY = "appIcon"
        private const val APP_LABEL_MANIFEST_PLACEHOLDER_KEY = "appLabel"
        private const val APP_FLAVOR_MANIFEST_PLACEHOLDER_KEY = "app_flavor"
        private const val APP_CONFIG_MANIFEST_PLACEHOLDER_KEY = "build_config"
        private const val APP_BACKEND_URL_MANIFEST_PLACEHOLDER_KEY = "backend_url"

        private object FlavorsField {
            // first is Type second is Name
            val BACKEND_URL = "String" to "BACKEND_URL"
            val MAPKIT_API_KEY = "String" to "MAPKIT_API_KEY"
        }
    }
}