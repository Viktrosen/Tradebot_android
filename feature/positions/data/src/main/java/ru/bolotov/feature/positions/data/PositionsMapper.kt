package ru.bolotov.feature.positions.data

import ru.bolotov.feature.positions.api.PositionsResponse
import ru.bolotov.feature.positions.domain.model.Position

fun PositionsResponse.toDomain(): List<Position> =
    openPositions.map { position ->
        val entryValue = position.entryPrice * position.quantity * position.lotSize
        val pnlPercent = position.unrealizedPnl
            ?.takeIf { entryValue != 0.0 }
            ?.div(entryValue)
            ?.times(100)

        Position(
            id = position.positionId,
            instrumentId = position.instrumentId,
            instrumentName = position.instrumentName,
            direction = position.direction,
            positionSide = position.positionSide,
            quantity = position.quantity,
            lotSize = position.lotSize,
            entryPrice = position.entryPrice,
            currentPrice = position.currentPrice,
            pnl = position.unrealizedPnl,
            pnlPercent = pnlPercent,
            isClosed = false,
            entryTime = position.entryTime,
            closeTime = null,
            entryStrategyName = position.entryStrategyName,
            aiExplanation = position.aiExplanation,
            closeExplanation = null,
            brokerStopLossPrice = position.brokerStopLossPrice,
            managedExitPrice = position.managedExitPrice,
            profitProtectionStage = position.profitProtectionStage
        )
    } + closedTrades.map { trade ->
        val entryValue = trade.entryPrice * trade.quantity * trade.lotSize
        Position(
            id = trade.positionId,
            instrumentId = trade.instrumentId,
            instrumentName = trade.instrumentName,
            direction = trade.direction,
            positionSide = trade.positionSide,
            quantity = trade.quantity,
            lotSize = trade.lotSize,
            entryPrice = trade.entryPrice,
            currentPrice = trade.closePrice,
            pnl = trade.realizedPnl,
            pnlPercent = if (entryValue == 0.0) null else trade.realizedPnl / entryValue * 100,
            isClosed = true,
            entryTime = trade.entryTime,
            closeTime = trade.closedAt,
            entryStrategyName = null,
            aiExplanation = null,
            closeExplanation = trade.closeExplanation
        )
    }
