package ru.bolotov.feature.positions.domain.interactor

import ru.bolotov.feature.positions.domain.model.Position

/** Доменная граница списка позиций и ручного закрытия позиции. */
interface PositionsInteractor {
    /** Загружает открытые и закрытые позиции в порядке убывания времени события. */
    suspend fun getPositions(): List<Position>

    /** Передаёт боту запрос на рыночное закрытие указанной позиции. */
    suspend fun closePosition(positionId: String)
}
