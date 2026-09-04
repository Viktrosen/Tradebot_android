package ru.bolotov.feature.instruments.api

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import ru.bolotov.core.network.NetworkApiProvider
import dagger.Module
import dagger.Provides

interface InstrumentsApi {
    @GET("command/instruments") suspend fun getInstruments(): InstrumentsResponse
    @POST("command/instruments/rescan") suspend fun rescanInstruments(): InstrumentsResponse
    @GET("command/instruments/filters") suspend fun getFilters(): InstrumentFiltersResponse
    @POST("command/instruments/filters") suspend fun updateFilters(@Body filters: InstrumentFiltersResponse): UpdateFiltersResponse
}

@Serializable data class InstrumentsResponse(val instruments: List<InstrumentResponse> = emptyList(), val count: Int = 0)
@Serializable
data class InstrumentResponse(
    val id: String,
    val ticker: String,
    val name: String,
    val tradingAvailable: Boolean = false
)
@Serializable data class InstrumentFiltersResponse(val minDailyVolume: Long, val minVolatility: Double, val maxVolatility: Double, val maxCount: Int)
@Serializable data class UpdateFiltersResponse(val status: String = "updated")

@Module
class InstrumentsApiModule {
    @Provides
    fun provideInstrumentsApi(networkApiProvider: NetworkApiProvider): InstrumentsApi =
        networkApiProvider.provideApiService(InstrumentsApi::class)
}
