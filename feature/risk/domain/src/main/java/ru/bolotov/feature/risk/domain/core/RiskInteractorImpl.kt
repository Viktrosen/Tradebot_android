package ru.bolotov.feature.risk.domain.core

import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.model.RiskConfig
import ru.bolotov.feature.risk.domain.repository.RiskRepository

class RiskInteractorImpl(
    private val repository: RiskRepository
) : RiskInteractor {
    override suspend fun getRiskConfig(): RiskConfig = repository.getRiskConfig()
    override suspend fun updateRiskConfig(config: RiskConfig): RiskConfig = repository.updateRiskConfig(config)
    override suspend fun resetRiskConfig(): RiskConfig = repository.resetRiskConfig()
}
