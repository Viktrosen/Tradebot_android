package ru.bolotov.core.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

/** All feature collectors share one socket; the last unsubscribe closes it immediately. */
class SharedTradingStateInteractor(
    source: TradingStateInteractor,
    scope: CoroutineScope,
) : TradingStateInteractor {
    private val shared = source.updates().shareIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 0),
        replay = 0,
    )

    override fun updates() = shared
}
