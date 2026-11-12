package ru.bolotov.feature.more.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import ru.bolotov.feature.more.domain.model.AppThemeMode

class ThemeRepositoryTest {
    @get:Rule val directory = TemporaryFolder()
    private val key = stringPreferencesKey("theme_mode")

    @Test
    fun `new installations default to SYSTEM`() = runBlocking {
        val job = SupervisorJob()
        try {
            assertEquals(AppThemeMode.SYSTEM, ThemeRepositoryImpl(store(job)).themeMode.first())
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun `all modes survive closing and reopening the store`() = runBlocking {
        for (mode in AppThemeMode.entries) {
            val fileName = "${mode.name.lowercase()}.preferences_pb"
            val job = SupervisorJob()
            try {
                ThemeRepositoryImpl(store(job, fileName)).setThemeMode(mode)
            } finally {
                job.cancelAndJoin()
            }
            val reopenedJob = SupervisorJob()
            try {
                assertEquals(
                    mode,
                    ThemeRepositoryImpl(store(reopenedJob, fileName)).themeMode.first(),
                )
            } finally {
                reopenedJob.cancelAndJoin()
            }
        }
    }

    @Test
    fun `unknown stored mode safely defaults to SYSTEM`() = runBlocking {
        val job = SupervisorJob()
        try {
            val store = store(job)
            store.edit { it[key] = "FUTURE_MODE" }
            assertEquals(AppThemeMode.SYSTEM, ThemeRepositoryImpl(store).themeMode.first())
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun `root host and settings observe the same committed change`() = runBlocking {
        val job = SupervisorJob()
        try {
            val repository = ThemeRepositoryImpl(store(job))
            withTimeout(5_000) {
                val firstReady = CompletableDeferred<Unit>()
                val secondReady = CompletableDeferred<Unit>()
                val first = async {
                    repository.themeMode.onEach { firstReady.complete(Unit) }.take(2).toList()
                }
                val second = async {
                    repository.themeMode.onEach { secondReady.complete(Unit) }.take(2).toList()
                }
                firstReady.await()
                secondReady.await()
                repository.setThemeMode(AppThemeMode.LIGHT)
                val expected = listOf(AppThemeMode.SYSTEM, AppThemeMode.LIGHT)
                assertEquals(expected, first.await())
                assertEquals(expected, second.await())
            }
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun `read IO failure unblocks startup and observation later recovers`() = runBlocking {
        var reads = 0
        val store =
            object : DataStore<Preferences> {
                override val data = flow {
                    if (reads++ == 0) throw IOException("disk unavailable")
                    emit(preferencesOf(key to "DARK"))
                }

                override suspend fun updateData(
                    transform: suspend (Preferences) -> Preferences
                ): Preferences = error("not used")
            }
        val modes = withTimeout(5_000) { ThemeRepositoryImpl(store).themeMode.take(2).toList() }
        assertEquals(listOf(AppThemeMode.SYSTEM, AppThemeMode.DARK), modes)
    }

    @Test
    fun `failed save does not publish an unsaved preference`() = runBlocking {
        val store =
            object : DataStore<Preferences> {
                override val data = flowOf(preferencesOf(key to "DARK"))

                override suspend fun updateData(
                    transform: suspend (Preferences) -> Preferences
                ): Preferences = throw IOException("disk full")
            }
        val repository = ThemeRepositoryImpl(store)
        try {
            repository.setThemeMode(AppThemeMode.LIGHT)
            fail("Must propagate persistence failure")
        } catch (_: IOException) {
            assertEquals(AppThemeMode.DARK, repository.themeMode.first())
        }
    }

    private fun store(job: kotlinx.coroutines.Job, fileName: String = "appearance.preferences_pb") =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(job + Dispatchers.IO),
            produceFile = { directory.root.resolve(fileName) },
        )
}
