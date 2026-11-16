package ru.bolotov.tradebot.navigation.deeplink

import android.content.Intent
import android.net.Uri
import ru.bolotov.tradebot.navigation.data.AppLink
import java.util.UUID

/** Normalizes native links, background FCM extras and previously delivered notifications. */
class AppLinkParser {
    fun parse(intent: Intent): AppLink? {
        val uri = intent.data
        if (uri != null) {
            parseUri(uri)?.let {
                return it
            }
            if (uri.scheme != AppLinkContract.SCHEME ||
                uri.host != AppLinkContract.LEGACY_NOTIFICATION_HOST) {
                return null
            }
        }
        return positionTarget(intent.getStringExtra(AppLinkContract.POSITION_ID))
    }

    fun parseNotificationData(data: Map<String, String>): AppLink? =
        positionTarget(data[AppLinkContract.POSITION_ID])

    private fun parseUri(uri: Uri): AppLink? {
        if (uri.scheme != AppLinkContract.SCHEME || uri.host != AppLinkContract.POSITIONS_HOST)
            return null
        return uri.pathSegments.singleOrNull()?.let(::positionTarget)
    }

    private fun positionTarget(value: String?): AppLink? {
        val id =
            value?.let { runCatching { UUID.fromString(it).toString() }.getOrNull() } ?: return null
        return AppLink.PositionDetails(positionId = id)
    }
}
