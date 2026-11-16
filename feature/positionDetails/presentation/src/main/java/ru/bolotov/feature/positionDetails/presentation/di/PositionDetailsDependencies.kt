package ru.bolotov.feature.positionDetails.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.feature.positionDetails.domain.interactor.PositionDetailsInteractor

interface PositionDetailsDependencies : Dependencies {
    val positionDetailsInteractor: PositionDetailsInteractor
}
