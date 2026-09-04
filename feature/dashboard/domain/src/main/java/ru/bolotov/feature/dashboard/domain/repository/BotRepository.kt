package ru.bolotov.feature.dashboard.domain.repository

import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig

interface BotRepository {
    suspend fun getBotStatus(): BotStatus
    suspend fun getPositions(): List<Position>
    suspend fun getDashboard(): Dashboard
    suspend fun startBot()
    suspend fun stopBot()
}
