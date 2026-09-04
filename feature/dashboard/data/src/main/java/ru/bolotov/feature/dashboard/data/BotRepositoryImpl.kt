package ru.bolotov.feature.dashboard.data

import ru.bolotov.feature.dashboard.api.BotApi
import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.Positions
import ru.bolotov.feature.dashboard.domain.model.RiskConfig
import ru.bolotov.feature.dashboard.domain.repository.BotRepository
import javax.inject.Inject

class BotRepositoryImpl @Inject constructor(
    private val api: BotApi
) : BotRepository {

    override suspend fun getBotStatus(): BotStatus {
        return try {
            api.getBotStatus().toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Ошибка получения статуса бота: ${e.message}")
        }
    }

    override suspend fun getPositions(): List<Position> {
        return try {
            api.getPositions().toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Ошибка получения позиций: ${e.message}")
        }
    }

    override suspend fun getDashboard(): Dashboard {
        return try {
            api.getDashboard().toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Не удалось получить данные Dashboard: ${e.message}")
        }
    }

    override suspend fun startBot() {
        try {
            api.startBot()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Ошибка запуска бота: ${e.message}")
        }
    }

    override suspend fun stopBot() {
        try {
            api.stopBot()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Ошибка остановки бота: ${e.message}")
        }
    }

}
