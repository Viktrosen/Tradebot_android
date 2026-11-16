package ru.bolotov.tradebot.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import ru.bolotov.tradebot.MainActivity
import ru.bolotov.tradebot.navigation.data.AppLink
import ru.bolotov.tradebot.navigation.deeplink.AppLinkEncoder

/** Builds the Activity task; Main's router separately owns the Compose back stack. */
class NotificationPendingIntentFactory(private val encoder: AppLinkEncoder) {
    fun create(context: Context, destination: AppLink?, notificationId: Int): PendingIntent {
        val intent =
            Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = destination?.let(encoder::encode)
            }
        return requireNotNull(
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(
                    notificationId,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
        )
    }
}
