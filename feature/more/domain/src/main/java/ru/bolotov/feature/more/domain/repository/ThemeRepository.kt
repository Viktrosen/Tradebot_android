package ru.bolotov.feature.more.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.bolotov.feature.more.domain.model.AppThemeMode

interface ThemeRepository {
    val themeMode: Flow<AppThemeMode>

    suspend fun setThemeMode(mode: AppThemeMode)
}
