package ru.bolotov.feature.positions.domain.repository

import ru.bolotov.feature.positions.domain.model.Position

interface PositionsRepository { suspend fun getPositions(): List<Position>; suspend fun closePosition(positionId: String) }
