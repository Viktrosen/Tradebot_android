package ru.bolotov.feature.auth.data

import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
class StoredTokens(
    val access: String,
    val refresh: String,
    val accessExpiresAt: String,
    val refreshExpiresAt: String,
    val email: String
)

/** Data-layer storage boundary; credentials never cross into domain or UI. */
interface TokenStore {
    fun read(): StoredTokens?
    fun write(tokens: StoredTokens)
    fun clear()
}

/** Both rotated credentials are committed together in app-provided encrypted preferences. */
class SecureTokenStore(private val preferences: SharedPreferences, private val json: Json) : TokenStore {
    override fun read(): StoredTokens? {
        val encoded = preferences.getString(KEY, null) ?: return null
        return try {
            json.decodeFromString<StoredTokens>(encoded).also {
                require(it.access.isNotBlank() && it.refresh.isNotBlank())
                java.time.Instant.parse(it.accessExpiresAt)
                java.time.Instant.parse(it.refreshExpiresAt)
            }
        } catch (_: IllegalArgumentException) {
            clear()
            null
        } catch (_: java.time.format.DateTimeParseException) {
            clear()
            null
        }
    }

    override fun write(tokens: StoredTokens) {
        if (!preferences.edit().putString(KEY, json.encodeToString(tokens)).commit()) throw IOException("Unable to persist session")
    }

    override fun clear() {
        if (!preferences.edit().remove(KEY).commit()) throw IOException("Unable to remove session")
    }

    private companion object { const val KEY = "auth_token_pair" }
}
