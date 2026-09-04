package ru.bolotov.feature.dashboard.router

import kotlinx.serialization.Serializable
import ru.bolotov.tradebot.router.AggregateFeatureEntry

@Serializable
sealed interface DashboardGraph {
    @Serializable
    data object Dashboard : DashboardGraph  // data object для лучшей поддержки

    @Serializable
    data object Root : DashboardGraph
}

abstract class DashboardEntry : AggregateFeatureEntry {
    // featureRoute теперь автоматически из FeatureEntry
    // или можно переопределить для обратной совместимости:
    override val featureRoute: String
        get() = "dashboard"  // оставляем для BottomBarItem и т.д.
}