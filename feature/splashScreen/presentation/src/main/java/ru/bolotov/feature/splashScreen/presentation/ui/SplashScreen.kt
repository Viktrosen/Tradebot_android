package ru.bolotov.feature.splashScreen.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import ru.bolotov.feature.splashScreen.presentation.core.Action
import ru.bolotov.feature.splashScreen.presentation.core.Effect
import ru.bolotov.feature.splashScreen.presentation.core.SplashViewModel

@Composable
internal fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToMainScreen: () -> Unit,
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(Unit) {
        delay(2000)  // Показываем анимацию 2 секунды
        viewModel.handleAction(Action.OnNavigateToMainScreen)
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is Effect.NavigateMainScreen -> onNavigateToMainScreen()
        }
    }

    SplashScreenContent()
}