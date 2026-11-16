package ru.bolotov.feature.positionDetails.domain.model

sealed interface PositionDetailsUpdate {
    data class Quote(val currentPrice: Double, val pnl: Double, val pnlPercent: Double) :
        PositionDetailsUpdate

    data object RefreshRequired : PositionDetailsUpdate
}
