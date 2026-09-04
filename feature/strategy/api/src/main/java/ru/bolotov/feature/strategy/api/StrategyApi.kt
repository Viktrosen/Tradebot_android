package ru.bolotov.feature.strategy.api

import dagger.Module
import dagger.Provides
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.bolotov.core.network.NetworkApiProvider

interface StrategyApi {
    @GET("command/strategy/available") suspend fun getStrategies(): StrategiesResponse
    @GET("command/strategy/configurations") suspend fun getConfigurations(): StrategyConfigurationsResponse
    @GET("command/status") suspend fun getBotStatus(): StrategyStatusResponse
    @POST("command/strategy/simple") suspend fun switchSimple(@Body request: SimpleRequest): SwitchResponse
    @POST("command/strategy/confirmation") suspend fun switchConfirmation(@Body request: ConfirmationRequest): SwitchResponse
    @POST("command/strategy/voting") suspend fun switchVoting(@Body weights: Map<String, Int>): SwitchResponse
    @POST("command/strategy/candlestick") suspend fun switchCandlestick(@Body request: CandlestickRequest): SwitchResponse
}
@Serializable data class StrategiesResponse(val strategies: List<StrategyResponse>, val current: CurrentStrategyResponse)
@Serializable data class StrategyStatusResponse(val currentStrategy: CurrentStrategyResponse)
@Serializable data class StrategyResponse(val name: String, val type: String, val description: String)
@Serializable data class CurrentStrategyResponse(
    val id: String = "",
    val name: String,
    val description: String,
    val type: String = "",
    val settings: StrategySettingsResponse = StrategySettingsResponse(),
    val minConfidence: Double? = null,
    val currentTimeframe: String? = null
)
@Serializable data class StrategySettingsResponse(
    val timeframe: String? = null,
    val minConfidence: Double? = null,
    val indicators: List<String>? = null,
    val weights: Map<String, Int>? = null
)
@Serializable data class StrategyConfigurationsResponse(
    val candlestick: StrategySettingsResponse = StrategySettingsResponse(),
    val voting: StrategySettingsResponse = StrategySettingsResponse(),
    val confirmation: StrategySettingsResponse = StrategySettingsResponse()
)
@Serializable data class SimpleRequest(val strategyName: String)
@Serializable data class ConfirmationRequest(val indicators: List<String>)
@Serializable data class CandlestickRequest(val timeframe: String, val minConfidence: Double)
@Serializable data class SwitchResponse(
    val status: String = "",
    val strategy: String? = null,
    val success: Boolean? = null
)
@Module class StrategyApiModule { @Provides fun provideStrategyApi(provider: NetworkApiProvider): StrategyApi = provider.provideApiService(StrategyApi::class) }
