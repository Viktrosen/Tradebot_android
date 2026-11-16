package ru.bolotov.feature.positionDetails.data.mapper

import ru.bolotov.core.network.TradingStateUpdate
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate

/** Never apply P&L from another lifecycle of the same instrument. */
fun TradingStateUpdate.toDetailsUpdate(positionId: String): PositionDetailsUpdate? =
    when (this) {
        is TradingStateUpdate.PositionPrice ->
            if (this.positionId == positionId) {
                PositionDetailsUpdate.Quote(
                    currentPrice = currentPrice,
                    pnl = unrealizedPnl,
                    pnlPercent = pnlPercent,
                )
            } else null
        TradingStateUpdate.PositionsChanged,
        TradingStateUpdate.Reconnected -> PositionDetailsUpdate.RefreshRequired
        else -> null
    }
