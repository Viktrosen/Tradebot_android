package ru.bolotov.feature.more.domain.interactor

/** Доменная команда экстренного закрытия портфеля. */
interface EmergencyCloseInteractor {
    /** Запрашивает закрытие всех позиций и остановку бота после подтверждения в UI. */
    suspend fun closeAllPositions()
}
