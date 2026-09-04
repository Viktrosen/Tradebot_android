package ru.bolotov.feature.risk.api

import dagger.Module
import dagger.Provides
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.bolotov.core.network.NetworkApiProvider

interface RiskApi {
    @GET("command/risk")
    suspend fun get(): RiskResponse

    @POST("command/risk/update")
    suspend fun update(@Body request: RiskRequest): RiskResponse

    @POST("command/risk/reset")
    suspend fun reset(): RiskResponse
}

@Serializable
data class RiskResponse(
    val success: Boolean,
    val config: RiskData
)

@Serializable
data class RiskData(
    val positionSizePercent: Double,
    val positionSizePercentDisplay: String,
    val stopLossPercent: Double,
    val stopLossPercentDisplay: String,
    val takeProfitPercent: Double,
    val takeProfitPercentDisplay: String,
    val maxCapitalUsage: Double,
    val maxCapitalUsagePercent: String,
    val maxPositions: Int,
    val shortTradingEnabled: Boolean = false
)

@Serializable
data class RiskRequest(
    val positionSizePercent: Double? = null,
    val stopLossPercent: Double? = null,
    val takeProfitPercent: Double? = null,
    val maxCapitalUsage: Double? = null,
    val maxPositions: Int? = null,
    val shortTradingEnabled: Boolean? = null
)

@Module
class RiskApiModule {
    @Provides
    fun provideRiskApi(networkProvider: NetworkApiProvider): RiskApi =
        networkProvider.provideApiService(RiskApi::class)
}
