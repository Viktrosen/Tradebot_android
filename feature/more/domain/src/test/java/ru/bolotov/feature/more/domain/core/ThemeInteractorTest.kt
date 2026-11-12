package ru.bolotov.feature.more.domain.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.bolotov.feature.more.domain.model.AppThemeMode
import ru.bolotov.feature.more.domain.repository.ThemeRepository

class ThemeInteractorTest {
    @Test
    fun `interactor forwards persisted modes and user selection`() = runBlocking {
        val repository =
            object : ThemeRepository {
                override val themeMode = MutableStateFlow(AppThemeMode.SYSTEM)

                override suspend fun setThemeMode(mode: AppThemeMode) {
                    themeMode.value = mode
                }
            }
        val interactor = ThemeInteractorImpl(repository)
        assertEquals(AppThemeMode.SYSTEM, interactor.themeMode.first())
        for (mode in AppThemeMode.entries) {
            interactor.setThemeMode(mode)
            assertEquals(mode, repository.themeMode.value)
            assertEquals(mode, interactor.themeMode.first())
        }
    }
}
