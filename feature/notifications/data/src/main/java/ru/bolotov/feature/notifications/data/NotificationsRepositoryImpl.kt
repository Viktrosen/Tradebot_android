package ru.bolotov.feature.notifications.data

import ru.bolotov.feature.notifications.api.DeviceTokenRequest
import ru.bolotov.feature.notifications.api.NotificationsApi
import ru.bolotov.feature.notifications.domain.repository.NotificationsRepository
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val notificationsApi: NotificationsApi
) : NotificationsRepository {
    override suspend fun registerDevice(installationId: String, fcmToken: String) {
        notificationsApi.registerDevice(
            DeviceTokenRequest(installationId = installationId, fcmToken = fcmToken)
        )
    }
}
