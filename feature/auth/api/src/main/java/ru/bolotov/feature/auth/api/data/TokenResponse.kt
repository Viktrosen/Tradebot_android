package ru.bolotov.feature.auth.api.data

import kotlinx.serialization.Serializable

@Serializable class LoginRequest(val email: String, val password: String)
@Serializable class RefreshRequest(val refreshToken: String)
@Serializable
class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: String,
    val refreshTokenExpiresAt: String
)
