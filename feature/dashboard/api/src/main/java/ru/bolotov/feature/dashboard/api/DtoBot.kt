package ru.bolotov.feature.dashboard.api

import kotlinx.serialization.Serializable

interface DtoBot {
    @Serializable
    data class BotStatusResponse(
        val running: Boolean,
        val currentStrategy: CurrentStrategy,  // ← теперь объект
        val activeInstruments: List<String>,
        val allTradingUnavailable: Boolean = false,
        val marketRegime: String? = null  // НОВОЕ: текущий режим рынка
    )

    @Serializable
    data class CurrentStrategy(
        val id: String = "",
        val name: String,
        val description: String,
        val type: String = "",
        val settings: StrategySettings = StrategySettings(),
        val minConfidence: Double? = null,
        val currentTimeframe: String? = null
    )

    @Serializable
    data class StrategySettings(
        val timeframe: String? = null,
        val minConfidence: Double? = null,
        val indicators: List<String>? = null,
        val weights: Map<String, Int>? = null
    )

    @Serializable
    data class PositionsResponse(
        val openPositions: List<PositionResponse>,
        val count: Int
    )

    @Serializable
    data class DashboardResponse(
        val openPositions: List<DashboardOpenPosition>,
        val closedTrades: List<ClosedTrade>,
        val metrics: DashboardMetrics
    )

    @Serializable
    data class DashboardOpenPosition(
        val positionId: String,
        val instrumentId: String,
        val instrumentName: String,
        val direction: String,
        val positionSide: String = "LONG",
        val entryPrice: Double,
        val currentPrice: Double? = null,
        val unrealizedPnl: Double? = null,
        val quantity: Long,
        val lotSize: Int,
        val entryCommission: Double,
        val entryTime: String,
        val entryStrategyName: String? = null,
        val brokerStopLossPrice: Double? = null,
        val managedExitPrice: Double? = null,
        val profitProtectionStage: String? = null
    )

    @Serializable
    data class ClosedTrade(
        val positionId: String,
        val instrumentId: String,
        val instrumentName: String,
        val direction: String,
        val positionSide: String = "LONG",
        val closePrice: Double,
        val quantity: Long,
        val lotSize: Int,
        val realizedPnl: Double,
        val closedAt: String
    )

    @Serializable
    data class DashboardMetrics(
        val realizedPnl: Double,
        val dailyPnl: Double,
        val winRate: Double,
        val closedTradesCount: Int,
        val availableCash: Double = 0.0
    )

    @Serializable
    data class PositionResponse(
        val positionId: String,
        val instrumentId: String,
        val instrumentName: String,
        val direction: String,
        val positionSide: String = "LONG",
        val entryPrice: Double,
        val quantity: Long,
        val entryTime: String?,
        val stopLossPrice: Double?,
        val atr: Double?,
        val pnl: Double?,
        val currentPrice: Double?,
        val entryStrategyName: String? = null,
        val brokerStopLossPrice: Double? = null,
        val managedExitPrice: Double? = null,
        val profitProtectionStage: String? = null
    )
    @Serializable
    data class RiskConfigResponse(
        val success: Boolean,
        val config: RiskConfigData
    )

    @Serializable
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

    @Serializable
    data class RiskUpdateRequest(
        val positionSizePercent: Double,
        val stopLossPercent: Double,
        val takeProfitPercent: Double,
        val maxCapitalUsage: Double,
        val maxPositions: Int
    )
}
