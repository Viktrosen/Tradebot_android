package ru.bolotov.beautyfinder.convention.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.InvalidPluginException
import org.gradle.kotlin.dsl.findByType
import ru.bolotov.beautyfinder.convention.configuration.configureCompose

class ComposeCorePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when(val ext = extensions.findByType(CommonExtension::class)){
                is LibraryExtension -> {
                    configureCompose(ext)
                }
                is ApplicationExtension -> {
                    configureCompose(ext)
                }
                else -> throw InvalidPluginException("extension of type ${(ext?:String)::class.java.name}")
            }
        }
    }
}
