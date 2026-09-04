package ru.bolotov.feature.strategy.domain.core
import ru.bolotov.feature.strategy.domain.interactor.StrategyInteractor
import ru.bolotov.feature.strategy.domain.model.Strategies
import ru.bolotov.feature.strategy.domain.repository.StrategyRepository
class StrategyInteractorImpl(private val repository: StrategyRepository): StrategyInteractor {
 override suspend fun getStrategies(): Strategies = repository.getStrategies(); override suspend fun simple(name:String)=repository.simple(name); override suspend fun confirmation(i:List<String>)=repository.confirmation(i); override suspend fun voting(w:Map<String,Int>)=repository.voting(w); override suspend fun candlestick(t:String,c:Double)=repository.candlestick(t,c)
}
