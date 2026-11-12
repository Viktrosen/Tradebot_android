package ru.bolotov.feature.auth.data.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.bolotov.feature.auth.data.repository.AuthRepositoryImpl

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
