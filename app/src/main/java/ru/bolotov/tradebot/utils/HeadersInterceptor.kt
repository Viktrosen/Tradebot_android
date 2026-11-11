package ru.bolotov.tradebot.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.TimeZone

/** Non-secret client metadata only. Session credentials belong to the auth data layer. */
class HeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(
        chain.request().newBuilder()
            .header("Accept-Language", Locale.getDefault().language)
            .header("User-Timezone", TimeZone.getDefault().id)
            .build()
    )
}
