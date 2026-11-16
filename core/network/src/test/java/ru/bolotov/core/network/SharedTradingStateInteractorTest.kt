package ru.bolotov.core.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedTradingStateInteractorTest {
    @Test
    fun `two screens share one transport and release it with the last subscriber`() = runTest {
        var opens = 0
        var closes = 0
        val source = object : TradingStateInteractor {
            override fun updates() = flow<TradingStateUpdate> {
                opens++
                try { awaitCancellation() } finally { closes++ }
            }
        }
        val shared = SharedTradingStateInteractor(source = source, scope = backgroundScope)
        val dashboard = backgroundScope.launch { shared.updates().collect {} }
        val details = backgroundScope.launch { shared.updates().collect {} }
        runCurrent()
        assertEquals(1, opens)
        details.cancel()
        runCurrent()
        assertEquals(0, closes)
        dashboard.cancel()
        runCurrent()
        assertEquals(1, closes)
    }
}
