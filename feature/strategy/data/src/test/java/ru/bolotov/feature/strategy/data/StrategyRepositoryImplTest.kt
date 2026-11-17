package ru.bolotov.feature.strategy.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import ru.bolotov.feature.strategy.api.CandlestickRequest
import ru.bolotov.feature.strategy.api.ConfirmationRequest
import ru.bolotov.feature.strategy.api.CurrentStrategyResponse
import ru.bolotov.feature.strategy.api.StrategiesResponse
import ru.bolotov.feature.strategy.api.StrategyApi
import ru.bolotov.feature.strategy.api.StrategyConfigurationsResponse
import ru.bolotov.feature.strategy.api.StrategyResponse
import ru.bolotov.feature.strategy.api.StrategySettingsResponse
import ru.bolotov.feature.strategy.api.SwitchResponse

class StrategyRepositoryImplTest {
    @Test
    fun `catalog preserves legacy current metadata and loads independent parameters`() = runBlocking {
        val api = RecordingStrategyApi()
        val repository = StrategyRepositoryImpl(api = api)

        val strategies = repository.getStrategies()

        assertEquals(listOf("catalog", "configurations"), api.calls)
        assertEquals("ema", strategies.current.id)
        assertEquals("Cross EMA", strategies.current.name)
        assertEquals("future", strategies.items.single().type)
        assertEquals(mapOf("EMA" to 3, "SUPER_TREND" to 2), strategies.configurations.voting.weights)
    }

    @Test
    fun `configuration requests retain payload without a selection request`() = runBlocking {
        val api = RecordingStrategyApi()
        val repository = StrategyRepositoryImpl(api = api)

        repository.voting(weights = mapOf("EMA" to 3, "RSI" to 2))
        repository.confirmation(indicators = listOf("EMA", "RSI"))
        repository.candlestick(timeframe = "M5", minConfidence = 0.9)

        assertEquals(listOf("voting", "confirmation", "candlestick"), api.calls)
        assertEquals(mapOf("EMA" to 3, "RSI" to 2), api.weights)
        assertEquals(listOf("EMA", "RSI"), api.confirmation?.indicators)
        assertEquals(CandlestickRequest(timeframe = "M5", minConfidence = 0.9), api.candlestick)
    }

    @Test
    fun `failed configuration fetch is not replaced with invented defaults`() = runBlocking {
        val api = RecordingStrategyApi()
        val failure = IllegalStateException("unavailable")
        api.failure = failure

        val result = runCatching { StrategyRepositoryImpl(api = api).getStrategies() }

        assertSame(failure, result.exceptionOrNull())
    }

    private class RecordingStrategyApi : StrategyApi {
        val calls = mutableListOf<String>()
        var weights: Map<String, Int>? = null
        var confirmation: ConfirmationRequest? = null
        var candlestick: CandlestickRequest? = null
        var failure: Exception? = null

        override suspend fun getStrategies(): StrategiesResponse {
            calls += "catalog"
            return StrategiesResponse(
                strategies = listOf(StrategyResponse(name = "New", type = "future", description = "")),
                current = CurrentStrategyResponse(name = "Cross EMA", description = "Legacy"),
            )
        }

        override suspend fun getConfigurations(): StrategyConfigurationsResponse {
            calls += "configurations"
            failure?.let { throw it }
            return StrategyConfigurationsResponse(
                voting = StrategySettingsResponse(weights = mapOf("EMA" to 3, "SUPER_TREND" to 2)),
            )
        }

        override suspend fun switchConfirmation(request: ConfirmationRequest): SwitchResponse {
            calls += "confirmation"
            confirmation = request
            return SwitchResponse()
        }

        override suspend fun switchVoting(weights: Map<String, Int>): SwitchResponse {
            calls += "voting"
            this.weights = weights
            return SwitchResponse()
        }

        override suspend fun switchCandlestick(request: CandlestickRequest): SwitchResponse {
            calls += "candlestick"
            candlestick = request
            return SwitchResponse()
        }
    }
}
