package ru.bolotov.feature.positionDetails.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.bolotov.feature.positionDetails.domain.model.PositionDetails
import ru.bolotov.feature.positionDetails.domain.model.PositionDetailsUpdate

interface PositionDetailsRepository {
    suspend fun getDetails(positionId: String): PositionDetails

    fun observeUpdates(positionId: String): Flow<PositionDetailsUpdate>

    suspend fun requestClose(positionId: String)
}
