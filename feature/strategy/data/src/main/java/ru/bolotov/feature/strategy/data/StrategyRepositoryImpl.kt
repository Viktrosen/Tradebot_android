package ru.bolotov.feature.strategy.data
import ru.bolotov.feature.strategy.api.CurrentStrategyResponse
import ru.bolotov.feature.strategy.api.StrategyApi
import ru.bolotov.feature.strategy.api.StrategyConfigurationsResponse
import ru.bolotov.feature.strategy.api.StrategySettingsResponse
import ru.bolotov.feature.strategy.api.CandlestickRequest
import ru.bolotov.feature.strategy.api.ConfirmationRequest
import ru.bolotov.feature.strategy.api.SimpleRequest
import ru.bolotov.feature.strategy.domain.model.CurrentStrategy
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.Strategy
import ru.bolotov.feature.strategy.domain.model.StrategySettings
import ru.bolotov.feature.strategy.domain.model.StrategyConfigurations
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository
import javax.inject.Inject
class StrategyRepositoryImpl @Inject constructor(private val api: StrategyApi) : StrategyRepository {
 override suspend fun getStrategies() = api.getStrategies().let { response ->
     val configurations = api.getConfigurations().toDomain()
     val currentStrategy = runCatching {
         api.getBotStatus().currentStrategy
     }.getOrDefault(response.current)

     Strategies(
         items = response.strategies.map { strategy ->
             Strategy(strategy.name, strategy.type, strategy.description)
         },
        current = currentStrategy.toDomain(),
        configurations = configurations
     )
 }
 override suspend fun simple(name:String){ api.switchSimple(SimpleRequest(name)) }
 override suspend fun confirmation(indicators:List<String>){ api.switchConfirmation(ConfirmationRequest(indicators)) }
 override suspend fun voting(weights:Map<String,Int>){ api.switchVoting(weights) }
 override suspend fun candlestick(timeframe:String,minConfidence:Double){ api.switchCandlestick(CandlestickRequest(timeframe,minConfidence)) }
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
    contains("candlestick", ignoreCase = true) -> "candlestick"
    contains("voting", ignoreCase = true) -> "voting"
    contains("confirmation", ignoreCase = true) -> "confirmation"
    contains("ema", ignoreCase = true) -> "ema"
    contains("rsi", ignoreCase = true) -> "rsi"
    contains("macd", ignoreCase = true) -> "macd"
    contains("bollinger", ignoreCase = true) || contains("bb", ignoreCase = true) -> "bb"
    else -> lowercase()
}

private fun String.toStrategyType(): String = when (toStrategyId()) {
    "candlestick" -> "candlestick"
    "voting" -> "voting"
    "confirmation" -> "confirmation"
    else -> "simple"
}
