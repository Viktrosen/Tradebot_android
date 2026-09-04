package ru.bolotov.feature.strategy.domain.interactor
import ru.bolotov.feature.strategy.domain.model.Strategies
/** Доменная граница независимых настроек торговых стратегий. */
interface StrategyInteractor {
    /** Возвращает доступные карточки и сохранённые конфигурации каждой стратегии. */
    suspend fun getStrategies(): Strategies

    /** Сохраняет настройки простой стратегии для совместимости с API TradeBot. */
    suspend fun simple(name: String)

    /** Сохраняет требуемые индикаторы стратегии подтверждения. */
    suspend fun confirmation(indicators: List<String>)

    /** Сохраняет веса сигналов стратегии голосования. */
    suspend fun voting(weights: Map<String, Int>)

    /** Сохраняет таймфрейм и минимальную уверенность свечной стратегии. */
    suspend fun candlestick(timeframe: String, minConfidence: Double)
}
