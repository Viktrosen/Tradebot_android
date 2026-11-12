package ru.bolotov.tradebot.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

class NetworkLoggingTest {
    @Test fun `debug logs method URL status and duration without credentials or bodies`() {
        val lines = mutableListOf<String>()
        val interceptor = createHttpLoggingInterceptor(true) { lines.add(it) }
        assertEquals(HttpLoggingInterceptor.Level.BASIC, interceptor.level)
        exchange(interceptor)
        val log = lines.joinToString("\n")
        assertTrue(log.contains("POST"))
        assertTrue(log.contains("/api/auth/login"))
        assertTrue(log.contains("200"))
        listOf("private-password", "private-access", "private-refresh", "private-cookie", "owner@example.com")
            .forEach { secret -> assertFalse("Credential appeared in HTTP log", log.contains(secret)) }
    }

    @Test fun `release logging is disabled`() {
        val lines = mutableListOf<String>()
        val interceptor = createHttpLoggingInterceptor(false) { lines.add(it) }
        assertEquals(HttpLoggingInterceptor.Level.NONE, interceptor.level)
        exchange(interceptor)
        assertTrue(lines.isEmpty())
    }

    private fun exchange(interceptor: HttpLoggingInterceptor) {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setHeader("Set-Cookie", "private-cookie")
                .setBody("""{"accessToken":"private-access","refreshToken":"private-refresh"}"""))
            server.start()
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val request = Request.Builder().url(server.url("/api/auth/login"))
                .header("Authorization", "Bearer private-access")
                .header("Cookie", "private-cookie")
                .post("""{"email":"owner@example.com","password":"private-password"}"""
                    .toRequestBody("application/json".toMediaType())).build()
            client.newCall(request).execute().use { assertEquals(200, it.code) }
        }
    }
}
