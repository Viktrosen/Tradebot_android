package ru.bolotov.feature.strategy.domain.model
data class Strategy(val name: String, val type: String, val description: String)
data class Strategies(
    val items: List<Strategy>,
    val current: CurrentStrategy,
    val configurations: StrategyConfigurations = StrategyConfigurations()
)

data class StrategyConfigurations(
    val candlestick: StrategySettings.Candlestick = StrategySettings.Candlestick("M5", 0.85),
    val voting: StrategySettings.Voting = StrategySettings.Voting(emptyMap()),
    val confirmation: StrategySettings.Confirmation = StrategySettings.Confirmation(emptyList())
)

data class CurrentStrategy(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val settings: StrategySettings
)

sealed interface StrategySettings {
    data object None : StrategySettings
    data class Candlestick(val timeframe: String, val minConfidence: Double) : StrategySettings
    data class Confirmation(val indicators: List<String>) : StrategySettings
    data class Voting(val weights: Map<String, Int>) : StrategySettings
}
