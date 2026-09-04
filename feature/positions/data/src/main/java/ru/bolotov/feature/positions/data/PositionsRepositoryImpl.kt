package ru.bolotov.feature.positions.data

import retrofit2.HttpException
import ru.bolotov.feature.positions.api.PositionsApi
import ru.bolotov.feature.positions.domain.model.ClosePositionUnavailableException
import ru.bolotov.feature.positions.domain.model.Position
import ru.bolotov.feature.positions.domain.repository.PositionsRepository
import javax.inject.Inject

class PositionsRepositoryImpl @Inject constructor(
    private val api: PositionsApi
) : PositionsRepository {
    override suspend fun getPositions(): List<Position> = try {
        api.getPositions().toDomain()
    } catch (error: Exception) {
        throw IllegalStateException("Не удалось загрузить позиции: ${error.message}", error)
    }

    override suspend fun closePosition(positionId: String) {
        try {
            api.closePosition(positionId)
        } catch (error: HttpException) {
            if (error.code() == 422) throw ClosePositionUnavailableException()
            throw IllegalStateException("Не удалось закрыть позицию: ${error.message}", error)
        } catch (error: Exception) {
            throw IllegalStateException("Не удалось закрыть позицию: ${error.message}", error)
        }
    }
}
