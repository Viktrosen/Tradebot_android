package ru.bolotov.feature.positionDetails.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.bolotov.feature.positionDetails.domain.model.PositionDetails
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate

interface PositionDetailsInteractor {
    suspend fun getDetails(positionId: String): PositionDetails

    fun observeUpdates(positionId: String): Flow<PositionDetailsUpdate>

    suspend fun requestClose(positionId: String)
}
