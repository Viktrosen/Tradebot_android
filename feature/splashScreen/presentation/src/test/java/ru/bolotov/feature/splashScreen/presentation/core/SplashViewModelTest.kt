package ru.bolotov.feature.splashScreen.presentation.core

import androidx.lifecycle.ViewModelStore
import ru.bolotov.core.testing.controlOrbit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    @Test
    fun `navigation action emits the main navigation effect without changing state`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val model = SplashViewModel()
            store.put("splash", model)
            controlOrbit(model)
            model.handleAction(Action.OnNavigateToMainScreen)
            runCurrent()
            assertEquals(Effect.NavigateMainScreen, model.container.sideEffectFlow.first())
            assertEquals(Unit, model.container.stateFlow.value)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
