package ru.bolotov.tradebot.notifications

import android.app.Application
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.bolotov.feature.auth.domain.AuthInteractor
import ru.bolotov.feature.auth.domain.AuthSession
import com.google.firebase.messaging.FirebaseMessaging
import ru.bolotov.feature.notifications.domain.interactor.NotificationsInteractor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
/** Регистрирует FCM-токен текущей Android-установки в TradebotBackend. */
class FcmTokenRegistrar @Inject constructor(
    private val application: Application,
    private val notificationsInteractor: NotificationsInteractor,
    private val auth: AuthInteractor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            auth.session.collect { session ->
                if (session is AuthSession.SignedIn) {
                    FirebaseMessaging.getInstance().token.addOnSuccessListener(::register)
                }
            }
        }
    }

    /** Безопасно отправляет новый токен в фоне; ошибка не блокирует запуск приложения. */
    fun register(fcmToken: String) {
        if (auth.session.value !is AuthSession.SignedIn) return
        if (fcmToken.isBlank()) return
        val installationId = Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: return

        scope.launch {
            runCatching {
                notificationsInteractor.registerDevice(installationId, fcmToken)
            }.onFailure { error ->
                Log.w("FcmTokenRegistrar", "Unable to register FCM token", error)
            }
        }
    }
}
