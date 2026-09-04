package ru.bolotov.feature.more.domain.core

import ru.bolotov.feature.more.domain.interactor.EmergencyCloseInteractor
import ru.bolotov.feature.more.domain.repository.EmergencyCloseRepository

class EmergencyCloseInteractorImpl(
    private val repository: EmergencyCloseRepository
) : EmergencyCloseInteractor {
    override suspend fun closeAllPositions() {
        repository.closeAllPositions()
    }
}
