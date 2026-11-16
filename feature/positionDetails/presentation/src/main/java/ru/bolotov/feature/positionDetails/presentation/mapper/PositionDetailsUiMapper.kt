package ru.bolotov.feature.positionDetails.presentation.mapper

import ru.bolotov.feature.positionDetails.domain.model.PositionDetails
import ru.bolotov.feature.positionDetails.presentation.core.PositionDetailsUiModel

internal fun PositionDetails.toUi() =
    PositionDetailsUiModel(
        positionId = positionId,
        instrumentId = instrumentId,
        instrumentName = instrumentName,
        positionSide = positionSide,
        quantity = quantity,
        lotSize = lotSize,
        entryPrice = entryPrice,
        currentPrice = currentPrice,
        pnl = pnl,
        pnlPercent = pnlPercent,
        isClosed = isClosed,
        entryTime = entryTime,
        entryStrategyName = entryStrategyName,
        aiExplanation = aiExplanation,
        brokerStopLossPrice = brokerStopLossPrice,
        managedExitPrice = managedExitPrice,
        profitProtectionStage = profitProtectionStage,
        takeProfitPrice = takeProfitPrice,
        closeTime = closeTime,
        closePrice = closePrice,
        closeExplanation = closeExplanation,
        closeReason = closeReason,
    )
