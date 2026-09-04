package ru.bolotov.feature.notifications.domain.core

import ru.bolotov.feature.notifications.domain.interactor.NotificationsInteractor
import ru.bolotov.feature.notifications.domain.repository.NotificationsRepository

class NotificationsInteractorImpl(
    private val repository: NotificationsRepository
) : NotificationsInteractor {
    override suspend fun registerDevice(installationId: String, fcmToken: String) {
        repository.registerDevice(installationId, fcmToken)
    }
}
