package ru.bolotov.feature.auth.data

import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.bolotov.feature.auth.api.AuthApi
import ru.bolotov.feature.auth.data.local.TokenStore
import ru.bolotov.feature.auth.data.local.data.StoredTokens
import ru.bolotov.feature.auth.data.network.BearerAuthenticator
import ru.bolotov.feature.auth.data.network.BearerInterceptor
import ru.bolotov.feature.auth.data.repository.AuthRepositoryImpl
import ru.bolotov.feature.auth.domain.model.AuthSession

class AuthRepositoryTest {
    private val server = MockWebServer().apply { start() }
    private val store = MemoryStore()
    private val api = Retrofit.Builder().baseUrl(server.url("/"))
        .client(OkHttpClient.Builder().callTimeout(1, TimeUnit.SECONDS).retryOnConnectionFailure(false).build())
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build().create(AuthApi::class.java)

    @After fun tearDown() { server.shutdown() }

    @Test fun `parallel old 401 responses rotate exactly once`() {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        server.enqueue(success("new"))
        val executor = Executors.newFixedThreadPool(4)
        try {
            val calls = (1..4).map { executor.submit<String?> { repository.refreshAfterUnauthorized("old") } }
            calls.forEach { assertEquals("new", it.get(3, TimeUnit.SECONDS)) }
            assertEquals(1, server.requestCount)
            assertEquals("new-refresh", store.value?.refresh)
            assertTrue(repository.session.value is AuthSession.SignedIn)
        } finally { executor.shutdownNow() }
    }

    @Test fun `temporary refresh error keeps credentials for retry`() = runBlocking {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        server.enqueue(MockResponse().setResponseCode(503))
        repository.restore()
        assertEquals(AuthSession.Unavailable, repository.session.value)
        assertEquals("old", store.value?.access)
        server.enqueue(success("new"))
        repository.restore()
        assertTrue(repository.session.value is AuthSession.SignedIn)
    }

    @Test fun `definitive refresh rejection signs out`() = runBlocking {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        server.enqueue(MockResponse().setResponseCode(401))
        repository.restore()
        assertEquals(AuthSession.SignedOut, repository.session.value)
        assertNull(store.value)
    }

    @Test fun `malformed response does not crash restore or destroy saved pair`() = runBlocking {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        server.enqueue(MockResponse().setBody("{}"))
        repository.restore()
        assertEquals(AuthSession.Unavailable, repository.session.value)
        assertEquals("old", store.value?.access)
    }

    @Test fun `logout removes local credentials even if server is unavailable`() = runBlocking {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        server.enqueue(MockResponse().setResponseCode(503))
        repository.logout()
        assertNull(store.value)
        assertEquals(AuthSession.SignedOut, repository.session.value)
        assertEquals("/api/auth/logout", server.takeRequest().path)
    }

    @Test fun `expired refresh never sends a request`() {
        store.value = StoredTokens("old", "refresh", Instant.now().minusSeconds(60).toString(), Instant.now().minusSeconds(1).toString(), "owner@example.com")
        val repository = AuthRepositoryImpl(api, store)
        assertNull(repository.accessToken())
        assertNull(store.value)
        assertEquals(0, server.requestCount)
    }

    @Test fun `HTTP 401 refreshes once and retries with new bearer without API key`() {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        val client = OkHttpClient.Builder().addInterceptor(BearerInterceptor(repository))
            .authenticator(BearerAuthenticator(repository)).build()
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(success("new"))
        server.enqueue(MockResponse().setBody("ok"))
        client.newCall(okhttp3.Request.Builder().url(server.url("/api/bot/command/status"))
            .header("X-API-Key", "obsolete").build()).execute().use { assertEquals(200, it.code) }
        val original = server.takeRequest()
        assertEquals("Bearer old", original.getHeader("Authorization"))
        assertNull(original.getHeader("X-API-Key"))
        assertEquals("/api/auth/refresh", server.takeRequest().path)
        assertEquals("Bearer new", server.takeRequest().getHeader("Authorization"))
    }

    @Test fun `second HTTP 401 ends session instead of infinite retries`() {
        store.value = pair("old")
        val repository = AuthRepositoryImpl(api, store)
        val client = OkHttpClient.Builder().addInterceptor(BearerInterceptor(repository))
            .authenticator(BearerAuthenticator(repository)).build()
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(success("new"))
        server.enqueue(MockResponse().setResponseCode(401))
        client.newCall(okhttp3.Request.Builder().url(server.url("/api/bot/command/status")).build())
            .execute().use { assertEquals(401, it.code) }
        assertEquals(3, server.requestCount)
        assertNull(store.value)
        assertEquals(AuthSession.SignedOut, repository.session.value)
    }

    private fun pair(access: String) = StoredTokens(access, "$access-refresh", Instant.now().plusSeconds(600).toString(), Instant.now().plusSeconds(3600).toString(), "owner@example.com")
    private fun success(access: String) = MockResponse().setHeader("Content-Type", "application/json").setBody(
        """{"accessToken":"$access","refreshToken":"$access-refresh","accessTokenExpiresAt":"${Instant.now().plusSeconds(600)}","refreshTokenExpiresAt":"${Instant.now().plusSeconds(3600)}"}"""
    )
    private class MemoryStore : TokenStore {
        var value: StoredTokens? = null
        override fun read() = value
        override fun write(tokens: StoredTokens) { value = tokens }
        override fun clear() { value = null }
    }
}
