package ru.bolotov.feature.positionDetails.domain.core

import ru.bolotov.feature.positionDetails.domain.interactor.PositionDetailsInteractor
import ru.bolotov.feature.positionDetails.domain.repository.PositionDetailsRepository

class PositionDetailsInteractorImpl(private val repository: PositionDetailsRepository) :
    PositionDetailsInteractor {
    override suspend fun getDetails(positionId: String) = repository.getDetails(positionId)

    override fun observeUpdates(positionId: String) = repository.observeUpdates(positionId)

    override suspend fun requestClose(positionId: String) = repository.requestClose(positionId)
}
