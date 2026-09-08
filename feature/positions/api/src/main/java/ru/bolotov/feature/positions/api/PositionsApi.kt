package ru.bolotov.feature.positions.api

import dagger.Module
import dagger.Provides
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.bolotov.core.network.NetworkApiProvider

interface PositionsApi {
    @GET("command/dashboard")
    suspend fun getPositions(): PositionsResponse

    @POST("command/positions/{positionId}/close")
    suspend fun closePosition(@Path("positionId") positionId: String): ClosePositionResponse
}

@Serializable
data class PositionsResponse(
    val openPositions: List<OpenPositionResponse>,
    val closedTrades: List<ClosedPositionResponse>
)

@Serializable
data class OpenPositionResponse(
    val positionId: String,
    val instrumentId: String,
    val instrumentName: String,
    val direction: String,
    val positionSide: String = "LONG",
    val entryPrice: Double,
    val currentPrice: Double? = null,
    val unrealizedPnl: Double? = null,
    val quantity: Long,
    val lotSize: Int,
    val entryTime: String,
    val entryStrategyName: String? = null,
    val aiExplanation: String? = null,
    val brokerStopLossPrice: Double? = null,
    val managedExitPrice: Double? = null,
    val profitProtectionStage: String? = null
)

@Serializable
data class ClosedPositionResponse(
    val positionId: String,
    val instrumentId: String,
    val instrumentName: String,
    val direction: String,
    val positionSide: String = "LONG",
    val entryPrice: Double,
    val entryTime: String?,
    val closePrice: Double,
    val quantity: Long,
    val lotSize: Int,
    val realizedPnl: Double,
    val closedAt: String,
    val closeExplanation: String? = null
)

@Serializable
data class ClosePositionResponse(val status: String, val positionId: String)

@Module
class PositionsApiModule {
    @Provides fun providePositionsApi(networkProvider: NetworkApiProvider): PositionsApi = networkProvider.provideApiService(PositionsApi::class)
}
