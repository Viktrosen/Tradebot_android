package ru.bolotov.feature.risk.data

import ru.bolotov.feature.risk.api.RiskApi
import ru.bolotov.feature.risk.api.RiskData
import ru.bolotov.feature.risk.api.RiskRequest
import ru.bolotov.feature.risk.api.RiskResponse
import ru.bolotov.feature.risk.domain.model.RiskConfig
import ru.bolotov.feature.risk.domain.model.RiskConfigData
import ru.bolotov.feature.risk.domain.repository.RiskRepository
import javax.inject.Inject

class RiskRepositoryImpl @Inject constructor(
    private val api: RiskApi
) : RiskRepository {
    override suspend fun getRiskConfig(): RiskConfig = api.get().toDomain()

    override suspend fun updateRiskConfig(config: RiskConfig): RiskConfig = api.update(
        config.toRequest()
    ).toDomain()

    override suspend fun resetRiskConfig(): RiskConfig = api.reset().toDomain()
}

private fun RiskResponse.toDomain() = RiskConfig(
    success = success,
    config = config.toDomain()
)

private fun RiskData.toDomain() = RiskConfigData(
    positionSizePercent = positionSizePercent,
    positionSizePercentDisplay = positionSizePercentDisplay,
    stopLossPercent = stopLossPercent,
    stopLossPercentDisplay = stopLossPercentDisplay,
    takeProfitPercent = takeProfitPercent,
    takeProfitPercentDisplay = takeProfitPercentDisplay,
    maxCapitalUsage = maxCapitalUsage,
    maxCapitalUsagePercent = maxCapitalUsagePercent,
    maxPositions = maxPositions,
    shortTradingEnabled = shortTradingEnabled
)

private fun RiskConfig.toRequest() = RiskRequest(
    positionSizePercent = config.positionSizePercent,
    stopLossPercent = config.stopLossPercent,
    takeProfitPercent = config.takeProfitPercent,
    maxCapitalUsage = config.maxCapitalUsage,
    maxPositions = config.maxPositions,
    shortTradingEnabled = config.shortTradingEnabled
)
