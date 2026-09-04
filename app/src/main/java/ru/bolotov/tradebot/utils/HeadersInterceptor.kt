package ru.bolotov.tradebot.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Response
import ru.bolotov.core.utils.UserToken
import java.util.Locale
import java.util.TimeZone

class HeadersInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val language = Locale.getDefault().language
        val timezone = TimeZone.getDefault().id
        val request = chain.request()
            .newBuilder()
            .addHeader(HEADER_ACCEPT_LANGUAGE, language)
            .addHeader(HEADER_USER_TIMEZONE, timezone)
            .apply {
                addHeader(HEADER_AUTHORIZATION, "android_key1")
            }
            .build()

        return chain.proceed(request)
    }

    private companion object {
        private const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
        private const val HEADER_USER_TIMEZONE = "User-Timezone"
        private const val HEADER_AUTHORIZATION = "X-API-Key"
    }
}