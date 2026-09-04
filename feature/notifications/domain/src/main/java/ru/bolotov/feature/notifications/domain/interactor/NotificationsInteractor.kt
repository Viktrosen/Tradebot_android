package ru.bolotov.feature.notifications.domain.interactor

/** Доменная граница регистрации устройства для доставки FCM push-уведомлений. */
interface NotificationsInteractor {
    /** Привязывает FCM-токен к стабильному идентификатору установки приложения. */
    suspend fun registerDevice(installationId: String, fcmToken: String)
}
