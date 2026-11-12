package ru.bolotov.feature.more.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore

/** One process-wide instance; DI supplies the application context, never an Activity. */
val Context.themeDataStore by
    preferencesDataStore(
        name = "appearance",
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    )
