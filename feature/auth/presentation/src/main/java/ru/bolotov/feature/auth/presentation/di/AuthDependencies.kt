package ru.bolotov.feature.auth.presentation.di

import ru.bolotov.core.dependency.Dependencies
import ru.bolotov.feature.auth.domain.interactor.AuthInteractor

interface AuthDependencies : Dependencies { val authInteractor: AuthInteractor }
