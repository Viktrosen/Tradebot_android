package ru.bolotov.feature.strategy.presentation.core

/** Неизвестные и одиночные алгоритмы остаются информационными карточками. */
enum class StrategyType {
    SIMPLE,
    VOTING,
    CONFIRMATION,
    CANDLESTICK;

    val configurationDialog: StrategyDialog?
        get() = when (this) {
            SIMPLE -> null
            VOTING -> StrategyDialog.Voting
            CONFIRMATION -> StrategyDialog.Confirmation
            CANDLESTICK -> StrategyDialog.Candlestick
        }

    companion object {
        fun fromCatalog(type: String): StrategyType = when (type.trim().lowercase()) {
            "voting" -> VOTING
            "confirmation" -> CONFIRMATION
            "candlestick" -> CANDLESTICK
            else -> SIMPLE
        }
    }
}
