package ru.bolotov.feature.instruments.domain.model

data class TradingInstrument(
    val id: String,
    val ticker: String,
    val name: String,
    val tradingAvailable: Boolean
)
data class InstrumentFilters(val minDailyVolume: Long, val minVolatility: Double, val maxVolatility: Double, val maxCount: Int)
