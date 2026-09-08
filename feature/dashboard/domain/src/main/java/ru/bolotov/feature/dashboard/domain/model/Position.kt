package ru.bolotov.feature.dashboard.domain.model

data class Positions(
    val positions: List<Position>
)

data class Position(
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

data class Dashboard(
    val openPositions: List<Position>,
    val metrics: DashboardMetrics
)

data class DashboardMetrics(
    val realizedPnl: Double,
    val dailyPnl: Double,
    val winRate: Double,
    val closedTradesCount: Int,
    val availableCash: Double
)
