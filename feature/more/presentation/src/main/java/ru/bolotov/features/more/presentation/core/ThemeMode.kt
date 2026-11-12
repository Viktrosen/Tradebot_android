package ru.bolotov.features.more.presentation.core

import ru.bolotov.feature.more.domain.model.AppThemeMode

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}

internal fun ThemeMode.toDomain(): AppThemeMode =
    when (this) {
        ThemeMode.LIGHT -> AppThemeMode.LIGHT
        ThemeMode.DARK -> AppThemeMode.DARK
        ThemeMode.SYSTEM -> AppThemeMode.SYSTEM
    }

internal fun AppThemeMode.toUi(): ThemeMode =
    when (this) {
        AppThemeMode.LIGHT -> ThemeMode.LIGHT
        AppThemeMode.DARK -> ThemeMode.DARK
        AppThemeMode.SYSTEM -> ThemeMode.SYSTEM
    }
