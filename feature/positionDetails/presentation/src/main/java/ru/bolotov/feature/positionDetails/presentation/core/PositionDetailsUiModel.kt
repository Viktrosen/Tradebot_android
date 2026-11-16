package ru.bolotov.feature.positionDetails.presentation.core

/** Presentation snapshot; nullable broker fields stay nullable and are never inferred. */
data class PositionDetailsUiModel(
    val positionId: String,
    val instrumentId: String,
    val instrumentName: String,
    val positionSide: String,
    val quantity: Long,
    val lotSize: Int,
    val entryPrice: Double,
    val currentPrice: Double? = null,
    val pnl: Double? = null,
    val pnlPercent: Double? = null,
    val isClosed: Boolean = false,
    val entryTime: String? = null,
    val entryStrategyName: String? = null,
    val aiExplanation: String? = null,
    val brokerStopLossPrice: Double? = null,
    val managedExitPrice: Double? = null,
    val profitProtectionStage: String? = null,
    val takeProfitPrice: Double? = null,
    val closeTime: String? = null,
    val closePrice: Double? = null,
    val closeExplanation: String? = null,
    val closeReason: String? = null,
)
