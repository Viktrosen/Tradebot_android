package ru.bolotov.feature.strategy.domain.repository

import ru.bolotov.feature.strategy.domain.model.Strategies

interface StrategyRepository {
    suspend fun getStrategies(): Strategies
    suspend fun confirmation(indicators: List<String>)
    suspend fun voting(weights: Map<String, Int>)
    suspend fun candlestick(timeframe: String, minConfidence: Double)
}
