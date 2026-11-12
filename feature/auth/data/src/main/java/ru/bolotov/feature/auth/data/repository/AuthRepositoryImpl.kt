package ru.bolotov.feature.auth.data.repository

import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ru.bolotov.feature.auth.api.AuthApi
import ru.bolotov.feature.auth.api.data.LoginRequest
import ru.bolotov.feature.auth.api.data.RefreshRequest
import ru.bolotov.feature.auth.api.data.TokenResponse
import ru.bolotov.feature.auth.data.local.TokenStore
import ru.bolotov.feature.auth.data.local.data.StoredTokens
import ru.bolotov.feature.auth.domain.exception.AuthUnavailable
import ru.bolotov.feature.auth.domain.exception.LoginRejected
import ru.bolotov.feature.auth.domain.model.AuthSession
import ru.bolotov.feature.auth.domain.repository.AuthRepository

/**
 * One lock serializes login/refresh/logout, including concurrent HTTP 401s.
 * Transport/5xx errors retain credentials; only a definitive refresh rejection ends the session.
 */
class AuthRepositoryImpl(private val api: AuthApi, private val store: TokenStore) : AuthRepository {
    private val lock = Any()
    private var tokens = store.read()
    private val mutableSession = MutableStateFlow<AuthSession>(if (tokens == null) AuthSession.SignedOut else AuthSession.Checking)
    override val session: StateFlow<AuthSession> = mutableSession.asStateFlow()

    override suspend fun restore(): Unit = withContext(Dispatchers.IO) {
        synchronized(lock) {
            if (tokens == null) { mutableSession.value = AuthSession.SignedOut; return@synchronized }
            mutableSession.value = AuthSession.Checking
            try { refreshLocked(force = true) } catch (_: IOException) { mutableSession.value = AuthSession.Unavailable }
        }
    }

    override suspend fun login(email: String, password: String): Unit = withContext(Dispatchers.IO) {
        synchronized(lock) {
            val response = try { execute(api.login(LoginRequest(email, password))) } catch (_: IOException) { throw AuthUnavailable() }
            response.useBody { body ->
                when {
                    response.code() == 401 -> throw LoginRejected()
                    !response.isSuccessful || body == null -> throw AuthUnavailable()
                    else -> save(body, email)
                }
            }
        }
    }

    override suspend fun logout(): Unit = withContext(Dispatchers.IO) {
        synchronized(lock) {
            // Clearing locally always wins, even when the remote logout cannot be delivered.
            try {
                val access = refreshLocked()
                if (access != null) api.logout("Bearer $access").execute().errorBody()?.close()
            } catch (_: IOException) {
                // Remote access remains bounded by JWT expiry; encrypted credentials are removed.
            } finally { clearLocked() }
        }
    }

    /** Called only from OkHttp worker threads. No Retrofit suspend/dispatcher dependency. */
    fun accessToken(): String? = synchronized(lock) { refreshLocked() }

    /** A second caller receiving an old 401 reuses the already rotated pair. */
    fun refreshAfterUnauthorized(failedAccess: String): String? = synchronized(lock) {
        val current = tokens ?: return@synchronized null
        if (current.access != failedAccess) return@synchronized current.access
        refreshLocked(force = true)
    }

    fun rejectAccess(access: String) = synchronized(lock) {
        if (tokens?.access == access) clearLocked()
    }

    private fun refreshLocked(force: Boolean = false): String? {
        val current = tokens ?: return null
        if (Instant.parse(current.refreshExpiresAt) <= Instant.now()) { clearLocked(); return null }
        if (!force && Instant.parse(current.accessExpiresAt) > Instant.now().plusSeconds(30)) return current.access
        val response = execute(api.refresh(RefreshRequest(current.refresh)))
        response.useBody { body ->
            if (response.code() in setOf(400, 401, 403)) { clearLocked(); return null }
            if (!response.isSuccessful || body == null) throw IOException("Session refresh unavailable")
            save(body, current.email)
        }
        return tokens?.access
    }

    private fun save(response: TokenResponse, email: String) {
        if (response.accessToken.isBlank() || response.refreshToken.isBlank()) throw IOException("Invalid session response")
        try {
            Instant.parse(response.accessTokenExpiresAt)
            Instant.parse(response.refreshTokenExpiresAt)
        } catch (_: java.time.format.DateTimeParseException) { throw IOException("Invalid session expiry") }
        val updated = StoredTokens(response.accessToken, response.refreshToken, response.accessTokenExpiresAt, response.refreshTokenExpiresAt, email)
        store.write(updated)
        tokens = updated
        mutableSession.value = AuthSession.SignedIn(email, updated.refresh.substringBefore('.'))
    }

    private fun clearLocked() {
        tokens = null
        mutableSession.value = AuthSession.SignedOut
        store.clear()
    }

    private inline fun <T> retrofit2.Response<T>.useBody(block: (T?) -> Unit) {
        try { block(body()) } finally { errorBody()?.close() }
    }

    private fun <T> execute(call: retrofit2.Call<T>): retrofit2.Response<T> = try {
        call.execute()
    } catch (e: kotlinx.serialization.SerializationException) {
        throw IOException("Invalid authentication response", e)
    }
}
