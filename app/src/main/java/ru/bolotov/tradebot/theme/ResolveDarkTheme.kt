package ru.bolotov.tradebot.theme

import ru.bolotov.feature.more.domain.model.AppThemeMode

/** Resolve SYSTEM at the UI boundary so configuration changes remain observable. */
internal fun AppThemeMode.resolveDarkTheme(systemDark: Boolean): Boolean =
    when (this) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }
