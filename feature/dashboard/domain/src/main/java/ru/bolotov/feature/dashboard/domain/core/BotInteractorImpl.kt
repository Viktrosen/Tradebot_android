package ru.bolotov.feature.dashboard.domain.core

import ru.bolotov.feature.dashboard.domain.interactor.DashboardInteractor
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.repository.BotRepository

class DashboardInteractorImpl(
    private val repository: BotRepository
) : DashboardInteractor {

    override suspend fun getBotStatus(): BotStatus {
        return repository.getBotStatus()
    }

    override suspend fun getPositions(): List<Position> {
        val positions = repository.getPositions()
        // Сортировка по времени открытия (сначала новые)
        return positions.sortedByDescending { it.entryTime }
    }

    override suspend fun getDashboard(): Dashboard {
        return repository.getDashboard()
    }

    override suspend fun startBot() {
        repository.startBot()
    }

    override suspend fun stopBot() {
        repository.stopBot()
    }

}
