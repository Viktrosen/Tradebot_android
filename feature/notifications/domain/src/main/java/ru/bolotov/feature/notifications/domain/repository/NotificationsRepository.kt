package ru.bolotov.feature.notifications.domain.repository

interface NotificationsRepository {
    suspend fun registerDevice(installationId: String, fcmToken: String)
}
