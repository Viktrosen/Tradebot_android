package ru.bolotov.feature.auth.data.local.data

import kotlinx.serialization.Serializable

@Serializable
class StoredTokens(
    val access: String,
    val refresh: String,
    val accessExpiresAt: String,
    val refreshExpiresAt: String,
    val email: String
)
