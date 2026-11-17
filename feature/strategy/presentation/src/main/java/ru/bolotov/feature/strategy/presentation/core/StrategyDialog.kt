package ru.bolotov.feature.strategy.presentation.core

sealed interface StrategyDialog {
    data object Confirmation : StrategyDialog
    data object Voting : StrategyDialog
    data object Candlestick : StrategyDialog
}
