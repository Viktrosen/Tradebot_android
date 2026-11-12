package ru.bolotov.feature.auth.data.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.bolotov.feature.auth.data.repository.AuthRepositoryImpl

class BearerInterceptor(private val sessions: AuthRepositoryImpl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessions.accessToken()
        val request = chain.request().newBuilder().removeHeader("X-API-Key")
            .apply { if (token != null) header("Authorization", "Bearer $token") else removeHeader("Authorization") }
            .build()
        return chain.proceed(request)
    }
}
