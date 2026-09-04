package ru.bolotov.feature.more.data

import ru.bolotov.feature.more.api.EmergencyCloseApi
import ru.bolotov.feature.more.domain.repository.EmergencyCloseRepository
import javax.inject.Inject

class EmergencyCloseRepositoryImpl @Inject constructor(
    private val api: EmergencyCloseApi
) : EmergencyCloseRepository {
    override suspend fun closeAllPositions() {
        try {
            api.closeAllPositions()
        } catch (error: Exception) {
            throw IllegalStateException("Не удалось запустить экстренное закрытие позиций", error)
        }
    }
}
