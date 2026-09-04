package ru.bolotov.tradebot.environment

import ru.bolotov.core.common.ProjectConfigurationInfo
import ru.bolotov.tradebot.BuildConfig
import javax.inject.Inject

class ProjectConfigurationInfoCore @Inject constructor() : ProjectConfigurationInfo {

    override fun getAppVersion(): String = if (isDebug()) {
        "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})" +
                "-${BuildConfig.FLAVOR}" +
                "-${BuildConfig.BUILD_TYPE}"
    } else {
        BuildConfig.VERSION_NAME
    }

    override fun isDebug(): Boolean = BuildConfig.DEBUG

    override fun getBuildTypeSuffix(): String = if (BuildConfig.DEBUG) "_debug" else ""

    override fun getBaseUrl(): String = BuildConfig.BACKEND_URL

    override fun getPackage(): String = BuildConfig.APPLICATION_ID

    override fun getFlavor(): ProjectConfigurationInfo.AppFlavor {
        if (isBuildFlavorContains("dev")) return ProjectConfigurationInfo.AppFlavor.DEV
        return ProjectConfigurationInfo.AppFlavor.PROD
    }

    private fun isBuildFlavorContains(substring: String): Boolean =
        BuildConfig.FLAVOR.lowercase().contains(substring)
}