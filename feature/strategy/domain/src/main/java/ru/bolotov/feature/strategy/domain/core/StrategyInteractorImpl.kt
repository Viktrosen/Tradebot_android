package ru.bolotov.feature.strategy.domain.core

import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.model.VotingWeights
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository

/** Изменяет параметры алгоритмов, не выбирая активную торговую стратегию. */
class StrategyInteractorImpl(
    private val repository: StrategyRepository,
) : StrategyInteractor {
    override suspend fun getStrategies(): Strategies = repository.getStrategies()

    override suspend fun confirmation(indicators: List<String>) =
        repository.confirmation(indicators = indicators)

    override suspend fun voting(weights: Map<String, Int>) =
        repository.voting(weights = VotingWeights.from(weights).toPayload())

    override suspend fun candlestick(timeframe: String, minConfidence: Double) =
        repository.candlestick(timeframe = timeframe, minConfidence = minConfidence)
}
