package ru.bolotov.feature.dashboard.domain.interactor

import ru.bolotov.feature.dashboard.domain.model.BotStatus
import ru.bolotov.feature.dashboard.domain.model.Dashboard
import ru.bolotov.feature.dashboard.domain.model.Position
import ru.bolotov.feature.dashboard.domain.model.RiskConfig

/** Доменная граница данных и команд, требуемых экрану Dashboard. */
interface DashboardInteractor {
    /** Возвращает запуск бота, доступность торгов и выбранные для сканирования инструменты. */
    suspend fun getBotStatus(): BotStatus
    /** Возвращает открытые позиции без агрегированных метрик. */
    suspend fun getPositions(): List<Position>
    /** Возвращает данные одного снимка Dashboard: метрики, средства и позиции. */
    suspend fun getDashboard(): Dashboard
    /** Запускает торговый цикл. */
    suspend fun startBot()
    /** Останавливает торговый цикл. */
    suspend fun stopBot()
}
