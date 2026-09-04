package ru.bolotov.feature.risk.domain.repository

import ru.bolotov.feature.risk.domain.model.RiskConfig

interface RiskRepository {
    suspend fun getRiskConfig(): RiskConfig
    suspend fun updateRiskConfig(config: RiskConfig): RiskConfig
    suspend fun resetRiskConfig(): RiskConfig
}
