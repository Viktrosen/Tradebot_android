package ru.bolotov.feature.more.domain.core

import ru.bolotov.feature.more.domain.interactor.ThemeInteractor
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.feature.more.domain.repository.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {
    override val themeMode = repository.themeMode

    override suspend fun setThemeMode(mode: AppThemeMode) = repository.setThemeMode(mode)
}
