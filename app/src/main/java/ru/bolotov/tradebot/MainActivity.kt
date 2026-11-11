package ru.bolotov.tradebot

import android.graphics.Color
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import ru.bolotov.tradebot.navigation.Navigation
import ru.bolotov.tradebot.navigation.AppNavigationState
import ru.bolotov.tradebot.navigation.AppViewModel
import ru.bolotov.tradebot.navigation.requiresNavigation
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectedViewModel
import ru.bolotov.feature.auth.router.AuthGraph
import ru.bolotov.feature.main.route.MainGraph
import ru.bolotov.tradebot.di.components.DaggerAppNavigationComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!BuildConfig.DEBUG) {
            window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        }
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            )
        )


        setContent {
            val appViewModel: AppViewModel = injectedViewModel(this@MainActivity) {
                DaggerAppNavigationComponent.builder().dependencies(findDependencies()).build().viewModel
            }
            TradebotTheme {
                key(appViewModel.navigationInstanceId) {
                    val navController = rememberNavController()
                    var displayedSessionId by rememberSaveable { mutableStateOf<String?>(null) }
                    Navigation(activity = this@MainActivity, globalNavController = navController)

                    LaunchedEffect(navController, appViewModel) {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            appViewModel.navigationState.collect { state ->
                                val current = navController.currentDestination
                                if (state.requiresNavigation(
                                        isAuthDestination = current?.hasRoute<AuthGraph.Root>() == true,
                                        isMainDestination = current?.hasRoute<MainGraph.Root>() == true,
                                        displayedSessionId = displayedSessionId
                                    )) {
                                    val target = when (state) {
                                        AppNavigationState.Auth -> AuthGraph.Root
                                        is AppNavigationState.Main -> MainGraph.Root
                                    }
                                    navController.navigate(target) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                        launchSingleTop = true
                                        // Never retain protected entries or their nested ViewModel stores.
                                        restoreState = false
                                    }
                                }
                                displayedSessionId = (state as? AppNavigationState.Main)?.sessionId
                            }
                        }
                    }
                }
            }
        }
    }
}
