package ru.bolotov.feature.auth.data

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class BearerInterceptor(private val sessions: AuthRepositoryImpl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessions.accessToken()
        val request = chain.request().newBuilder().removeHeader("X-API-Key")
            .apply { if (token != null) header("Authorization", "Bearer $token") else removeHeader("Authorization") }
            .build()
        return chain.proceed(request)
    }
}

class BearerAuthenticator(private val sessions: AuthRepositoryImpl) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val failed = response.request.header("Authorization")?.removePrefix("Bearer ") ?: return null
        if (response.priorResponse != null) {
            sessions.rejectAccess(failed)
            return null
        }
        val token = sessions.refreshAfterUnauthorized(failed) ?: return null
        return response.request.newBuilder().header("Authorization", "Bearer $token").build()
    }
}
