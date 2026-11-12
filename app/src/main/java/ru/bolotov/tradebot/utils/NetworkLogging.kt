package ru.bolotov.tradebot.utils

import okhttp3.logging.HttpLoggingInterceptor

/** Debug-only HTTP metadata. Never enable BODY: login/refresh and FCM payloads contain secrets. */
internal fun createHttpLoggingInterceptor(
    isDebug: Boolean,
    logger: HttpLoggingInterceptor.Logger
): HttpLoggingInterceptor = HttpLoggingInterceptor(logger).apply {
    level = if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    // Defense in depth if the metadata level is changed later.
    listOf("Authorization", "Proxy-Authorization", "Cookie", "Set-Cookie", "X-API-Key")
        .forEach(::redactHeader)
}
