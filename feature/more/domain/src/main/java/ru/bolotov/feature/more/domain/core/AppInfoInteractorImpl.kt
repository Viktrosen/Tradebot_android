package ru.bolotov.feature.more.domain.core

import ru.bolotov.feature.more.domain.interactor.AppInfoInteractor

class AppInfoInteractorImpl(
    private val appVersion: String,
    private val buildNumber: String
) : AppInfoInteractor {

    override fun getAppVersion(): String = appVersion

    override fun getBuildNumber(): String = buildNumber
}