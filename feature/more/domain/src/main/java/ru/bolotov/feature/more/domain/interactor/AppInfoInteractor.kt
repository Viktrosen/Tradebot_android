package ru.bolotov.feature.more.domain.interactor

interface AppInfoInteractor {
    fun getAppVersion(): String
    fun getBuildNumber(): String
}