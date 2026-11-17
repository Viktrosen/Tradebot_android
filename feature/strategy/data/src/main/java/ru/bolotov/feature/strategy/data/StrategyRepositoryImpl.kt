package ru.bolotov.feature.strategy.data

import ru.bolotov.feature.strategy.api.CurrentStrategyResponse
import ru.bolotov.feature.strategy.api.StrategyApi
import ru.bolotov.feature.strategy.api.StrategyConfigurationsResponse
import ru.bolotov.feature.strategy.api.StrategySettingsResponse
import ru.bolotov.feature.strategy.api.CandlestickRequest
import ru.bolotov.feature.strategy.api.ConfirmationRequest
import ru.bolotov.feature.strategy.domain.model.CurrentStrategy
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.Strategy
import ru.bolotov.feature.strategy.domain.model.StrategySettings
import ru.bolotov.feature.strategy.domain.model.StrategyConfigurations
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository
import javax.inject.Inject

class StrategyRepositoryImpl @Inject constructor(private val api: StrategyApi) : StrategyRepository {
    override suspend fun getStrategies(): Strategies {
        val response = api.getStrategies()
        return Strategies(
            items = response.strategies.map { strategy ->
                Strategy(name = strategy.name, type = strategy.type, description = strategy.description)
            },
            current = response.current.toDomain(),
            configurations = api.getConfigurations().toDomain(),
        )
    }

    override suspend fun confirmation(indicators: List<String>) {
        api.switchConfirmation(request = ConfirmationRequest(indicators = indicators))
    }

    override suspend fun voting(weights: Map<String, Int>) {
        api.switchVoting(weights = weights)
    }

    override suspend fun candlestick(timeframe: String, minConfidence: Double) {
        api.switchCandlestick(
            request = CandlestickRequest(timeframe = timeframe, minConfidence = minConfidence),
        )
    }
}

private fun StrategyConfigurationsResponse.toDomain() = StrategyConfigurations(
    candlestick = StrategySettings.Candlestick(
        timeframe = candlestick.timeframe ?: "M5",
        minConfidence = candlestick.minConfidence ?: 0.85
    ),
    voting = StrategySettings.Voting(voting.weights.orEmpty()),
    confirmation = StrategySettings.Confirmation(confirmation.indicators.orEmpty())
)

private fun CurrentStrategyResponse.toDomain() = CurrentStrategy(
    id = id.ifBlank { name.toStrategyId() },
    name = name,
    type = type.ifBlank { name.toStrategyType() },
    description = description,
    settings = settings.toDomain(
        type = type.ifBlank { name.toStrategyType() },
        legacyTimeframe = currentTimeframe,
        legacyMinConfidence = minConfidence
    )
)

private fun StrategySettingsResponse.toDomain(
    type: String,
    legacyTimeframe: String?,
    legacyMinConfidence: Double?
): StrategySettings = when (type.lowercase()) {
    "candlestick" -> StrategySettings.Candlestick(
        timeframe = timeframe ?: legacyTimeframe ?: "M15",
        minConfidence = minConfidence ?: legacyMinConfidence ?: 0.85
    )
    "confirmation" -> StrategySettings.Confirmation(indicators.orEmpty())
    "voting" -> StrategySettings.Voting(weights.orEmpty())
    else -> StrategySettings.None
}

private fun String.toStrategyId(): String = when {
    contains(other = "candlestick", ignoreCase = true) -> "candlestick"
    contains(other = "voting", ignoreCase = true) -> "voting"
    contains(other = "confirmation", ignoreCase = true) -> "confirmation"
    contains(other = "ema", ignoreCase = true) -> "ema"
    contains(other = "rsi", ignoreCase = true) -> "rsi"
    contains(other = "macd", ignoreCase = true) -> "macd"
    contains(other = "bollinger", ignoreCase = true) || contains(other = "bb", ignoreCase = true) -> "bb"
    else -> lowercase()
}

private fun String.toStrategyType(): String = when (toStrategyId()) {
    "candlestick" -> "candlestick"
    "voting" -> "voting"
    "confirmation" -> "confirmation"
    else -> "simple"
}
