package ru.bolotov.feature.dashboard.domain.model

data class RiskConfig(
    val success: Boolean,
    val config: RiskConfigData
)

data class RiskConfigData(
    val positionSizePercent: Double,
    val positionSizePercentDisplay: String,
    val stopLossPercent: Double,
    val stopLossPercentDisplay: String,
    val takeProfitPercent: Double,
    val takeProfitPercentDisplay: String,
    val maxCapitalUsage: Double,
    val maxCapitalUsagePercent: String,
    val maxPositions: Int,
    val shortTradingEnabled: Boolean = false
)
