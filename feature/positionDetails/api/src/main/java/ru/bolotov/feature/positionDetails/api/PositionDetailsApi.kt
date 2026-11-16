package ru.bolotov.feature.positionDetails.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.bolotov.feature.positionDetails.api.data.response.PositionDetailsResponse

interface PositionDetailsApi {
    @GET("command/positions/{positionId}/details")
    suspend fun getDetails(@Path("positionId") positionId: String): PositionDetailsResponse
}
