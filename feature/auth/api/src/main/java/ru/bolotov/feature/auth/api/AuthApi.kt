package ru.bolotov.feature.auth.api

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/** Separate unauthenticated client: refresh must never call its own Authenticator. */
interface AuthApi {
    @POST("/api/auth/login") fun login(@Body request: LoginRequest): Call<TokenResponse>
    @POST("/api/auth/refresh") fun refresh(@Body request: RefreshRequest): Call<TokenResponse>
    @POST("/api/auth/logout") fun logout(@Header("Authorization") bearer: String): Call<Unit>
}

@Serializable class LoginRequest(val email: String, val password: String)
@Serializable class RefreshRequest(val refreshToken: String)
@Serializable
class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: String,
    val refreshTokenExpiresAt: String
)
