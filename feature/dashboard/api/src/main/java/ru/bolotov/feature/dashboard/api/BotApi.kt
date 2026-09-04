package ru.bolotov.feature.dashboard.api

import dagger.Module
import dagger.Provides
import retrofit2.http.GET
import retrofit2.http.POST
import ru.bolotov.core.network.NetworkApiProvider

interface BotApi {
    @GET("command/status")
    suspend fun getBotStatus(): DtoBot.BotStatusResponse

    @GET("command/positions")
    suspend fun getPositions(): DtoBot.PositionsResponse

    @GET("command/dashboard")
    suspend fun getDashboard(): DtoBot.DashboardResponse

    @POST("command/start")
    suspend fun startBot()

    @POST("command/stop")
    suspend fun stopBot()

}

@Module
class BotApiModule {

    @Provides
    fun provideBotServiceApi(networkProvider: NetworkApiProvider): BotApi =
        networkProvider.provideApiService(BotApi::class)
}
