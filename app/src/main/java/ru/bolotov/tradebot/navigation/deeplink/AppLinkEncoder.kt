package ru.bolotov.tradebot.navigation.deeplink

import android.net.Uri
import ru.bolotov.tradebot.navigation.data.AppLink

class AppLinkEncoder {
    fun encode(destination: AppLink): Uri =
        when (destination) {
            is AppLink.PositionDetails ->
                Uri.Builder()
                    .scheme(AppLinkContract.SCHEME)
                    .authority(AppLinkContract.POSITIONS_HOST)
                    .appendPath(destination.positionId)
                    .build()
        }
}
