package ru.bolotov.feature.more.domain.repository

interface EmergencyCloseRepository {
    suspend fun closeAllPositions()
}
