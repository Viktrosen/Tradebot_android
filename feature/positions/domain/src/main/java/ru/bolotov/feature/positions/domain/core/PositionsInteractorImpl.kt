package ru.bolotov.feature.positions.domain.core

import ru.bolotov.feature.positions.domain.interactor.PositionsInteractor
import ru.bolotov.feature.positions.domain.model.Position
import ru.bolotov.feature.positions.domain.repository.PositionsRepository

class PositionsInteractorImpl(private val repository: PositionsRepository) : PositionsInteractor {
    override suspend fun getPositions(): List<Position> = repository.getPositions().sortedByDescending { it.closeTime ?: it.entryTime }
    override suspend fun closePosition(positionId: String) = repository.closePosition(positionId)
}
