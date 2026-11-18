package ru.bolotov.feature.risk.presentation.core

import androidx.lifecycle.ViewModelStore
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.bolotov.core.utils.AnalyticManager
import ru.bolotov.core.testing.controlOrbit
import ru.bolotov.feature.risk.domain.interactor.RiskInteractor
import ru.bolotov.feature.risk.domain.model.RiskConfig
import ru.bolotov.feature.risk.domain.model.RiskConfigData

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class RiskViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val interactor = FakeRisk()

    @Before
    fun setUp() { Dispatchers.setMain(dispatcher) }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load maps risk configuration and actions edit draft without saving`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.UpdatePositionSizePercent(value = 0.1))
        model.handleAction(Action.UpdateStopLossPercent(value = 0.015))
        model.handleAction(Action.UpdateTakeProfitPercent(value = 0.04))
        model.handleAction(Action.UpdateMaxCapitalUsage(value = 0.6))
        model.handleAction(Action.UpdateMaxPositions(value = 3))
        val state = model.container.stateFlow.first { it.riskConfig?.maxPositions == 3 }

        assertFalse(state.isLoading)
        assertEquals(0.1, state.riskConfig!!.positionSizePercent, 0.0)
        assertEquals(0.015, state.riskConfig!!.stopLossPercent, 0.0)
        assertEquals(0.04, state.riskConfig!!.takeProfitPercent, 0.0)
        assertEquals(0.6, state.riskConfig!!.maxCapitalUsage, 0.0)
        assertTrue(interactor.saves.isEmpty())
    }

    @Test
    fun `short enable requires confirmation and disabling is immediate`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.RequestShortTradingEnabled(value = true))
        val requested = model.container.stateFlow.first { it.isShortTradingConfirmationVisible }
        assertFalse(requested.riskConfig!!.shortTradingEnabled)
        model.handleAction(Action.DismissShortTradingConfirmation)
        assertFalse(model.container.stateFlow.first { !it.isShortTradingConfirmationVisible }.riskConfig!!.shortTradingEnabled)

        model.handleAction(Action.RequestShortTradingEnabled(value = true))
        model.container.stateFlow.first { it.isShortTradingConfirmationVisible }
        model.handleAction(Action.ConfirmShortTradingEnabled)
        val enabled = model.container.stateFlow.first { it.riskConfig?.shortTradingEnabled == true }
        assertFalse(enabled.isShortTradingConfirmationVisible)
        model.handleAction(Action.RequestShortTradingEnabled(value = false))
        assertFalse(model.container.stateFlow.first { it.riskConfig?.shortTradingEnabled == false }.isShortTradingConfirmationVisible)
        assertTrue(interactor.saves.isEmpty())
    }

    @Test
    fun `save passes edited configuration and displays server-confirmed result`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.UpdateMaxPositions(value = 3))
        model.container.stateFlow.first { it.riskConfig?.maxPositions == 3 }
        interactor.savedOverride = interactor.profile.copy(config = interactor.profile.config.copy(maxPositions = 2))
        model.handleAction(Action.Save)
        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        val state = model.container.stateFlow.first { !it.isSaving && it.riskConfig?.maxPositions == 2 }
        assertEquals(3, interactor.saves.single().config.maxPositions)
        assertNotNull(state.successMessage)
        assertNull(state.errorMessage)
    }

    @Test
    fun `save failure retains edited settings and clears progress`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.UpdateStopLossPercent(value = 0.01))
        model.container.stateFlow.first { it.riskConfig?.stopLossPercent == 0.01 }
        interactor.saveFailure = IOException("save failed")
        model.handleAction(Action.Save)

        assertEquals(Effect.ShowError("save failed"), model.container.sideEffectFlow.first())
        assertFalse(model.container.stateFlow.value.isSaving)
        assertEquals(0.01, model.container.stateFlow.value.riskConfig!!.stopLossPercent, 0.0)
        assertNull(model.container.stateFlow.value.successMessage)
    }

    @Test
    fun `load failure is visible and refresh can recover`() = runTest(dispatcher) {
        interactor.loadFailure = IOException("offline")
        val model = model()
        assertEquals("offline", model.container.stateFlow.first { it.errorMessage != null }.errorMessage)
        assertEquals(Effect.ShowError("offline"), model.container.sideEffectFlow.first())
        interactor.loadFailure = null
        model.handleAction(Action.Refresh)
        val recovered = model.container.stateFlow.first { it.riskConfig != null }
        assertFalse(recovered.isLoading)
        assertNull(recovered.errorMessage)
    }

    @Test
    fun `reset dialog actions emit effects and confirmed reset uses server snapshot`() = runTest(dispatcher) {
        val model = loadedModel()
        model.handleAction(Action.ShowResetConfirmation)
        assertEquals(Effect.ShowResetConfirmation, model.container.sideEffectFlow.first())
        model.handleAction(Action.DismissResetConfirmation)
        assertEquals(Effect.DismissResetConfirmation, model.container.sideEffectFlow.first())
        model.handleAction(Action.ConfirmReset)
        assertEquals(Effect.DismissResetConfirmation, model.container.sideEffectFlow.first())
        assertTrue(model.container.sideEffectFlow.first() is Effect.ShowToast)
        val reset = model.container.stateFlow.first { it.riskConfig?.maxPositions == 10 }
        assertFalse(reset.isResetting)
        assertEquals(1, interactor.resets)
    }

    @Test
    fun `reset failure does not invent reset values or leave spinner active`() = runTest(dispatcher) {
        val model = loadedModel()
        interactor.resetFailure = IOException("reset failed")
        model.handleAction(Action.ConfirmReset)

        assertEquals(Effect.ShowError("reset failed"), model.container.sideEffectFlow.first())
        val state = model.container.stateFlow.value
        assertFalse(state.isResetting)
        assertEquals(5, state.riskConfig!!.maxPositions)
    }

    @Test
    fun `duplicate save cannot submit another request while first is pending`() = runTest(dispatcher) {
        val model = loadedModel()
        val completion = CompletableDeferred<Unit>()
        interactor.saveCompletion = completion
        model.handleAction(Action.Save)
        model.container.stateFlow.first { it.isSaving }
        runCurrent()
        assertEquals(1, interactor.saves.size)
        model.handleAction(Action.Save)
        runCurrent()
        assertEquals(1, interactor.saves.size)
        completion.complete(Unit)
        model.container.stateFlow.first { !it.isSaving }
    }

    private suspend fun TestScope.model() = RiskViewModel(
        analyticManager = mock(AnalyticManager::class.java), riskInteractor = interactor
    ).also {
        store.put(key = "risk", viewModel = it)
        controlOrbit(it)
    }

    private suspend fun TestScope.loadedModel(): RiskViewModel = model().also {
        it.container.stateFlow.first { state -> state.riskConfig != null }
    }

    private class FakeRisk : RiskInteractor {
        val profile = RiskConfig(success = true, config = RiskConfigData(
            positionSizePercent = 0.05, stopLossPercent = 0.02, takeProfitPercent = 0.03,
            maxCapitalUsage = 0.8, maxPositions = 5
        ))
        var loadFailure: Exception? = null
        var saveFailure: Exception? = null
        var resetFailure: Exception? = null
        var saveCompletion: CompletableDeferred<Unit>? = null
        var savedOverride: RiskConfig? = null
        val saves = mutableListOf<RiskConfig>()
        var resets = 0

        override suspend fun getRiskConfig(): RiskConfig { loadFailure?.let { throw it }; return profile }
        override suspend fun updateRiskConfig(config: RiskConfig): RiskConfig {
            saves += config
            saveCompletion?.await()
            saveFailure?.let { throw it }
            return savedOverride ?: config
        }
        override suspend fun resetRiskConfig(): RiskConfig {
            resets++
            resetFailure?.let { throw it }
            return profile.copy(config = profile.config.copy(maxPositions = 10))
        }
    }
}
