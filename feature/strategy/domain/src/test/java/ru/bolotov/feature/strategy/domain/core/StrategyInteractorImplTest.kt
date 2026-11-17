package ru.bolotov.feature.strategy.domain.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.bolotov.feature.strategy.domain.model.CurrentStrategy
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.StrategySettings
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository

class StrategyInteractorImplTest {
    private val repository = RecordingStrategyRepository()
    private val interactor = StrategyInteractorImpl(repository = repository)

    @Test
    fun `saves parameters without selecting a global strategy`() = runBlocking {
        val weights = mapOf("EMA" to 3, "SUPER_TREND" to 2)
        val indicators = listOf("EMA", "RSI")

        interactor.voting(weights = weights)
        interactor.confirmation(indicators = indicators)
        interactor.candlestick(timeframe = "M5", minConfidence = 0.9)

        assertEquals(weights, repository.weights)
        assertEquals(indicators, repository.indicators)
        assertEquals(StrategySettings.Candlestick(timeframe = "M5", minConfidence = 0.9), repository.candlestick)
        assertSame(repository.strategies, interactor.getStrategies())
    }

    @Test
    fun `invalid voting parameters never reach the repository`() = runBlocking {
        val invalid = listOf(
            mapOf("EMA" to 0, "RSI" to 3),
            mapOf("EMA" to 11, "RSI" to 3),
            mapOf("EMA" to 2),
            mapOf("EMA" to 3, "FUTURE_INDICATOR" to 4)
        )

        invalid.forEach { weights ->
            assertTrue(runCatching { interactor.voting(weights = weights) }.isFailure)
            assertNull(repository.weights)
        }
    }

    @Test
    fun `configuration failure is returned to presentation unchanged`() = runBlocking {
        val failure = IllegalStateException("unavailable")
        repository.failure = failure

        val result = runCatching { interactor.voting(weights = mapOf("EMA" to 3, "RSI" to 2)) }

        assertSame(failure, result.exceptionOrNull())
    }

    private class RecordingStrategyRepository : StrategyRepository {
        val strategies = Strategies(
            items = emptyList(),
            current = CurrentStrategy(
                id = "market_regime",
                name = "Automatic",
                type = "automatic",
                description = "",
                settings = StrategySettings.None,
            ),
        )
        var weights: Map<String, Int>? = null
        var indicators: List<String>? = null
        var candlestick: StrategySettings.Candlestick? = null
        var failure: Exception? = null

        override suspend fun getStrategies(): Strategies = strategies
        override suspend fun confirmation(indicators: List<String>) {
            this.indicators = indicators
        }

        override suspend fun voting(weights: Map<String, Int>) {
            failure?.let { throw it }
            this.weights = weights
        }

        override suspend fun candlestick(timeframe: String, minConfidence: Double) {
            candlestick = StrategySettings.Candlestick(timeframe = timeframe, minConfidence = minConfidence)
        }
    }
}
