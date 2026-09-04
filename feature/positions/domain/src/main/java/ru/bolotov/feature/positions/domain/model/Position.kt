package ru.bolotov.feature.positions.domain.model

data class Position(
    val id: String,
    val instrumentId: String,
    val instrumentName: String,
    val direction: String,
    val positionSide: String = "LONG",
    val quantity: Long,
    val lotSize: Int,
    val entryPrice: Double,
    val currentPrice: Double?,
    val pnl: Double?,
    val pnlPercent: Double?,
    val isClosed: Boolean,
    val entryTime: String?,
    val closeTime: String?,
    val entryStrategyName: String?,
    val aiExplanation: String?,
    val closeExplanation: String?
)
