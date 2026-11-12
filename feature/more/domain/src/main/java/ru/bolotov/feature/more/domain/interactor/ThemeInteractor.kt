package ru.bolotov.feature.more.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.bolotov.feature.more.domain.model.AppThemeMode

interface ThemeInteractor {
    val themeMode: Flow<AppThemeMode>

    suspend fun setThemeMode(mode: AppThemeMode)
}
