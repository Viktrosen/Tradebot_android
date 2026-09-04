package ru.bolotov.feature.dashboard.domain.model

data class BotStatus(
    val running: Boolean,
    val currentStrategy: CurrentStrategy,  // ← теперь объект
    val activeInstruments: List<String>,
    val allTradingUnavailable: Boolean = false,
    val marketRegime: String? = null  // НОВОЕ: строковое представление режима рынка
)

data class CurrentStrategy(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val settings: StrategySettings
)

data class StrategySettings(
    val timeframe: String? = null,
    val minConfidence: Double? = null,
    val indicators: List<String> = emptyList(),
    val weights: Map<String, Int> = emptyMap()
)
