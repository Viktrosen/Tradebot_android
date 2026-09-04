package ru.bolotov.feature.dashboard.domain.model

/**
 * Текущий режим рынка, определённый Trading Engine.
 *
 * Режимы EXTREME_VOLATILE и UNCERTAIN означают, что новые позиции не открываются.
 */
enum class MarketRegime {
    STRONG_UPTREND,
    STRONG_DOWNTREND,
    WEAK_TREND,
    FLAT_LOW_VOL,
    FLAT_HIGH_VOL,
    VOLATILE,
    EXTREME_VOLATILE,
    UNCERTAIN;

    fun getLabel(): String = when (this) {
        STRONG_UPTREND -> "📈 Сильный рост"
        STRONG_DOWNTREND -> "📉 Сильное падение"
        WEAK_TREND -> "📊 Слабый тренд"
        FLAT_LOW_VOL -> "➡️ Тихий боковик"
        FLAT_HIGH_VOL -> "🌊 Волатильный боковик"
        VOLATILE -> "⚡ Волатильность"
        EXTREME_VOLATILE -> "🚫 Экстремальная волатильность"
        UNCERTAIN -> "❓ Неопределённость"
    }

    fun isTradingAllowed(): Boolean = this !in listOf(EXTREME_VOLATILE, UNCERTAIN)

    fun isWarning(): Boolean = this == EXTREME_VOLATILE
}
