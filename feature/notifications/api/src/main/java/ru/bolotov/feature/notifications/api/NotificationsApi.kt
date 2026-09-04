package ru.bolotov.feature.notifications.api

import dagger.Module
import dagger.Provides
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import ru.bolotov.core.network.NetworkApiProvider

interface NotificationsApi {
    @POST("/api/notifications/devices")
    suspend fun registerDevice(@Body request: DeviceTokenRequest): DeviceTokenResponse
}

@Serializable
data class DeviceTokenRequest(
    val installationId: String,
    val fcmToken: String,
    val platform: String = "android"
)

@Serializable
data class DeviceTokenResponse(
    val registered: Boolean,
    val updated: Boolean
)

@Module
class NotificationsApiModule {
    @Provides
    fun provideNotificationsApi(networkApiProvider: NetworkApiProvider): NotificationsApi =
        networkApiProvider.provideApiService(NotificationsApi::class)
}
