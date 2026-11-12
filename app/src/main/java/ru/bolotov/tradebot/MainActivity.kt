package ru.bolotov.tradebot

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.dependency.injectViewModel
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.feature.auth.router.AuthGraph
import ru.bolotov.feature.main.route.MainGraph
import ru.bolotov.tradebot.di.components.DaggerAppNavigationComponent
import ru.bolotov.tradebot.navigation.AppViewModel
import ru.bolotov.tradebot.navigation.Navigation
import ru.bolotov.tradebot.navigation.data.AppNavigationState
import ru.bolotov.tradebot.navigation.data.requiresNavigation
import ru.bolotov.tradebot.theme.resolveDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!BuildConfig.DEBUG) {
            window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        }
        super.onCreate(savedInstanceState)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1,
            )
        }

        val appViewModel =
            injectViewModel<AppViewModel>(this) {
                DaggerAppNavigationComponent.builder()
                    .dependencies(findDependencies())
                    .build()
                    .viewModel
            }
        splashScreen.setKeepOnScreenCondition { appViewModel.themeMode.value == null }

        setContent {
            val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()
            val resolvedMode = themeMode ?: return@setContent
            val darkTheme = resolvedMode.resolveDarkTheme(isSystemInDarkTheme())
            SideEffect {
                val barStyle =
                    if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle
                )
            }
            TradebotTheme(darkTheme = darkTheme) {
                key(appViewModel.navigationInstanceId) {
                    val navController = rememberNavController()
                    var displayedSessionId by rememberSaveable { mutableStateOf<String?>(null) }
                    Navigation(activity = this@MainActivity, globalNavController = navController)

                    LaunchedEffect(navController, appViewModel) {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            appViewModel.navigationState.collect { state ->
                                val current = navController.currentDestination
                                if (
                                    state.requiresNavigation(
                                        isAuthDestination =
                                            current?.hasRoute<AuthGraph.Root>() == true,
                                        isMainDestination =
                                            current?.hasRoute<MainGraph.Root>() == true,
                                        displayedSessionId = displayedSessionId,
                                    )
                                ) {
                                    val target =
                                        when (state) {
                                            AppNavigationState.Auth -> AuthGraph.Root
                                            is AppNavigationState.Main -> MainGraph.Root
                                        }
                                    navController.navigate(target) {
                                        popUpTo(navController.graph.id) { inclusive = true }
                                        launchSingleTop = true
                                        // Never retain protected entries or their nested ViewModel
                                        // stores.
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
