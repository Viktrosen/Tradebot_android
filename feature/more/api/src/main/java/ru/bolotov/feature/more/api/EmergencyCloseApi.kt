package ru.bolotov.feature.more.api

import dagger.Module
import dagger.Provides
import retrofit2.http.POST
import ru.bolotov.core.network.NetworkApiProvider

interface EmergencyCloseApi {
    @POST("command/close-all")
    suspend fun closeAllPositions()
}

@Module
class EmergencyCloseApiModule {
    @Provides
    fun provideEmergencyCloseApi(networkProvider: NetworkApiProvider): EmergencyCloseApi =
        networkProvider.provideApiService(EmergencyCloseApi::class)
}
