package ru.bolotov.core.common

interface ProjectConfigurationInfo {

    fun getAppVersion(): String

    fun isDebug(): Boolean

    fun getBuildTypeSuffix(): String

    fun getFlavor(): AppFlavor

    fun getBaseUrl(): String

    fun getPackage(): String

    enum class AppFlavor(val suffix: String) {
        DEV(suffix = "_dev"),
        PROD(suffix = "")
    }
}