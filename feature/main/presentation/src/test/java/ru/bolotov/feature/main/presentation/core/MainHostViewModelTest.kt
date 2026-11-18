package ru.bolotov.feature.main.presentation.core

import androidx.lifecycle.ViewModelStore
import ru.bolotov.core.testing.controlOrbit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainHostViewModelTest {
    @Test
    fun `section actions replace the current route and allow clearing it`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val model = MainHostViewModel()
            store.put("main", model)
            controlOrbit(model)
            assertNull(model.container.stateFlow.value.currentSection)
            model.handleAction(Action.UpdateCurrentSection(route = "positions"))
            runCurrent()
            assertEquals("positions", model.container.stateFlow.value.currentSection)
            model.handleAction(Action.UpdateCurrentSection(route = "dashboard"))
            runCurrent()
            assertEquals("dashboard", model.container.stateFlow.value.currentSection)
            model.handleAction(Action.UpdateCurrentSection(route = null))
            runCurrent()
            assertNull(model.container.stateFlow.value.currentSection)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
