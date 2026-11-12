package ru.bolotov.feature.more.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.feature.more.domain.repository.ThemeRepository

class ThemeRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ThemeRepository {
    override val themeMode =
        dataStore.data
            .retryWhen { error, attempt ->
                if (error !is IOException) return@retryWhen false
                // Do not block startup on a disk read failure. Keep observing so a
                // later successful read/write can still update the global palette.
                emit(emptyPreferences())
                delay((attempt.coerceAtMost(29) + 1) * 1_000L)
                true
            }
            .map { preferences ->
                AppThemeMode.entries.firstOrNull { it.name == preferences[THEME_MODE] }
                    ?: AppThemeMode.SYSTEM
            }
            .distinctUntilChanged()

    override suspend fun setThemeMode(mode: AppThemeMode) {
        // No optimistic in-memory copy: both hosts observe only persisted values.
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
