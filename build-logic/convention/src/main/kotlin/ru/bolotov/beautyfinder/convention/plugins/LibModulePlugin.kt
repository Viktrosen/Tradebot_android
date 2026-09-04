package ru.bolotov.beautyfinder.convention.plugins

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import ru.bolotov.beautyfinder.convention.configuration.configureCommon
import ru.bolotov.beautyfinder.convention.utils.PluginAlias

class LibModulePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            pluginManager.apply{
                apply(PluginAlias.ANDROID_LIBRARY)
                apply(PluginAlias.KOTLIN_ANDROID)
            }
            extensions.configure<LibraryExtension>{
                configureCommon(this)
            }
            // Тестовые зависимости для всех модулей
            dependencies {
                add("testImplementation", "junit:junit:4.13.2")
                add("androidTestImplementation", "androidx.test.ext:junit:1.2.1")
                add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.6.1")
            }
        }
    }
}
