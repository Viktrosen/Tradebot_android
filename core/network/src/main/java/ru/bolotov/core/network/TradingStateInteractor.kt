package ru.bolotov.core.network

import kotlinx.coroutines.flow.Flow

/**
 * Источник типизированных изменений торгового состояния в реальном времени.
 * Реализация не должна требовать от feature-модулей знания STOMP или WebSocket.
 */
interface TradingStateInteractor {
    /** Открывает поток изменений цены, портфеля, позиций, статуса и доступности торгов. */
    fun updates(): Flow<TradingStateUpdate>
}

/** Набор событий, которые UI может применить без повторного разбора сетевого payload. */
sealed interface TradingStateUpdate {
    data class PositionPrice(
        val instrumentId: String,
        val currentPrice: Double,
        val unrealizedPnl: Double,
        val pnlPercent: Double
    ) : TradingStateUpdate

    data class Portfolio(
        val totalValue: Double?,
        val availableCash: Double?,
        val blockedCash: Double?
    ) : TradingStateUpdate

    data object PositionsChanged : TradingStateUpdate
    data class BotStatus(val running: Boolean) : TradingStateUpdate

    data class TradingAvailability(
        val instruments: Map<String, Boolean>,
        val allTradingUnavailable: Boolean
    ) : TradingStateUpdate
}
