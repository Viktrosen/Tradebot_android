package ru.bolotov.feature.positionDetails.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.mapNotNull
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.positionDetails.api.PositionDetailsApi
import ru.bolotov.feature.positionDetails.data.mapper.toDetailsUpdate
import ru.bolotov.feature.positionDetails.data.mapper.toDomain
import ru.bolotov.feature.positionDetails.domain.repository.PositionDetailsRepository
import ru.bolotov.feature.positions.domain.repository.PositionsRepository

class PositionDetailsRepositoryImpl
@Inject
constructor(
    private val api: PositionDetailsApi,
    private val updates: TradingStateInteractor,
    private val positions: PositionsRepository,
) : PositionDetailsRepository {
    override suspend fun getDetails(positionId: String) = api.getDetails(positionId).toDomain()

    override fun observeUpdates(positionId: String) =
        updates.updates().mapNotNull { it.toDetailsUpdate(positionId) }

    override suspend fun requestClose(positionId: String) = positions.closePosition(positionId)
}
