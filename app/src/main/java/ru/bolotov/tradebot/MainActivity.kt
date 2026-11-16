package ru.bolotov.tradebot

import android.Manifest
import android.content.Intent
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.bolotov.core.dependency.findDependencies
import ru.bolotov.core.uikit.TradebotTheme
import ru.bolotov.tradebot.di.components.AppNavigationDependencies
import ru.bolotov.tradebot.navigation.AppNavigationHost
import ru.bolotov.tradebot.navigation.AppViewModel
import ru.bolotov.tradebot.navigation.AppViewModelFactory
import ru.bolotov.tradebot.navigation.deeplink.AppLinkParser
import ru.bolotov.tradebot.theme.resolveDarkTheme

class MainActivity : ComponentActivity() {
    private lateinit var appViewModel: AppViewModel
    private val appLinkParser = AppLinkParser()

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

        appViewModel =
            ViewModelProvider(
                owner = this,
                factory =
                    AppViewModelFactory(
                        dependencies = findDependencies<AppNavigationDependencies>()
                    ),
            )[AppViewModel::class.java]
        if (savedInstanceState == null) {
            acceptExternalIntent(intent = intent)
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
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
            }
            TradebotTheme(darkTheme = darkTheme) {
                AppNavigationHost(
                    activity = this@MainActivity,
                    navigationState = appViewModel.navigationState,
                    navigationInstanceId = appViewModel.navigationInstanceId,
                    onLinkHandled = appViewModel::acknowledgeLink,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        acceptExternalIntent(intent = intent)
    }

    private fun acceptExternalIntent(intent: Intent) {
        val destination = appLinkParser.parse(intent) ?: return
        appViewModel.acceptLink(destination = destination)
    }
}
