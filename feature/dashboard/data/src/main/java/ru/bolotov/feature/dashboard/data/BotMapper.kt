package ru.bolotov.feature.dashboard.data

import ru.bolotov.feature.dashboard.api.DtoBot
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.CurrentStrategy
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.DashboardMetrics
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.model.RiskConfigData
import ru.bolotov.feature.dashboard.domain.model.StrategySettings

fun DtoBot.BotStatusResponse.toDomain(): BotStatus = BotStatus(
    running = running,
    currentStrategy = currentStrategy.toDomain(),
    activeInstruments = activeInstruments,
    allTradingUnavailable = allTradingUnavailable
)

fun DtoBot.CurrentStrategy.toDomain(): CurrentStrategy = CurrentStrategy(
    id = id,
    name = name,
    description = description,
    type = type,
    settings = settings.toDomain(
        legacyTimeframe = currentTimeframe,
        legacyMinConfidence = minConfidence
    )
)

private fun DtoBot.StrategySettings.toDomain(
    legacyTimeframe: String?,
    legacyMinConfidence: Double?
) = StrategySettings(
    timeframe = timeframe ?: legacyTimeframe,
    minConfidence = minConfidence ?: legacyMinConfidence,
    indicators = indicators.orEmpty(),
    weights = weights.orEmpty()
)

fun DtoBot.PositionsResponse.toDomain(): List<Position> {
    return openPositions.map { it.toDomain() }
}

fun DtoBot.DashboardResponse.toDomain(): Dashboard = Dashboard(
    openPositions = openPositions.map { position ->
        Position(
            positionId = position.positionId,
            instrumentId = position.instrumentId,
            instrumentName = position.instrumentName,
            direction = position.direction,
            positionSide = position.positionSide,
            entryPrice = position.entryPrice,
            currentPrice = position.currentPrice,
            quantity = position.quantity,
            entryTime = position.entryTime,
            stopLossPrice = null,
            atr = null,
            pnl = position.unrealizedPnl,
            entryStrategyName = position.entryStrategyName
        )
    },
    metrics = DashboardMetrics(
        realizedPnl = metrics.realizedPnl,
        dailyPnl = metrics.dailyPnl,
        winRate = metrics.winRate,
        closedTradesCount = metrics.closedTradesCount,
        availableCash = metrics.availableCash
    )
)

fun DtoBot.PositionResponse.toDomain(): Position = Position(
    positionId = positionId,
    instrumentId = instrumentId,
    instrumentName = instrumentName,
    direction = direction,
    positionSide = positionSide,
    entryPrice = entryPrice,
    quantity = quantity,
    entryTime = entryTime,
    stopLossPrice = stopLossPrice,
    atr = atr,
    pnl = pnl,
    currentPrice = currentPrice,
    entryStrategyName = entryStrategyName
)

fun DtoBot.RiskConfigResponse.toDomain(): RiskConfig = RiskConfig(
    success = success,
    config = config.toDomain()
)

fun DtoBot.RiskConfigData.toDomain(): RiskConfigData = RiskConfigData(
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
