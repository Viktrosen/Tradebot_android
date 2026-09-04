package ru.bolotov.tradebot.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.bolotov.tradebot.TradebotApplication
import ru.bolotov.tradebot.R

/** Получает FCM и отображает системное уведомление TradeBot на устройстве. */
class TradebotMessagingService : FirebaseMessagingService() {
    /** Повторно регистрирует устройство при обновлении FCM-токена Firebase. */
    override fun onNewToken(token: String) {
        (application as TradebotApplication).fcmTokenRegistrar.register(token)
    }

    /** Создаёт канал и показывает уведомление, если FCM содержит notification payload. */
    override fun onMessageReceived(message: RemoteMessage) {
        val channelId = "tradebot_updates"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(channelId, "TradeBot", NotificationManager.IMPORTANCE_HIGH))
        val notification = message.notification ?: return
        manager.notify(System.currentTimeMillis().toInt(), NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .build())
    }
}
