package ru.bolotov.feature.auth.api

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import ru.bolotov.feature.auth.api.data.LoginRequest
import ru.bolotov.feature.auth.api.data.RefreshRequest
import ru.bolotov.feature.auth.api.data.TokenResponse

/** Separate unauthenticated client: refresh must never call its own Authenticator. */
interface AuthApi {
    @POST("/api/auth/login") fun login(@Body request: LoginRequest): Call<TokenResponse>
    @POST("/api/auth/refresh") fun refresh(@Body request: RefreshRequest): Call<TokenResponse>
    @POST("/api/auth/logout") fun logout(@Header("Authorization") bearer: String): Call<Unit>
}
