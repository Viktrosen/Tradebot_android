package ru.bolotov.core.utils.testing

import java.util.Collections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.internal.TestContainerDecorator

/**
 * Orbit 10's event loop uses Default independently of Dispatchers.Main. Complete
 * real constructor initialization, preserve its state/effects, then use Orbit's
 * own test-container seam to put subsequent actions on this test's virtual clock.
 * No production dispatchers, constructors or business rules are modified.
 *
 * Fixtures must clear their ViewModelStore; runTest cancels the test delegate's
 * background scope. Constructor dependencies must not block indefinitely.
 */
@OptIn(ExperimentalCoroutinesApi::class, OrbitInternal::class)
suspend fun <STATE : Any, EFFECT : Any> TestScope.controlOrbit(model: ContainerHost<STATE, EFFECT>) {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val initialEffects = Collections.synchronizedList(mutableListOf<EFFECT>())
    val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        model.container.sideEffectFlow.collect(initialEffects::add)
    }
    runCurrent()
    model.container.joinIntents()
    runCurrent()
    collector.cancelAndJoin()
    val container = model.container
    val initialState = container.stateFlow.value
    check(container is TestContainerDecorator<STATE, EFFECT>) { "Expected Orbit's testable container" }
    container.test(
        initialState = initialState,
        settings = container.settings.copy(eventLoopDispatcher = dispatcher, intentLaunchingDispatcher = dispatcher),
        testScope = backgroundScope
    )
    container.inlineOrbit { initialEffects.forEach { postSideEffect(it) } }
    runCurrent()
}
