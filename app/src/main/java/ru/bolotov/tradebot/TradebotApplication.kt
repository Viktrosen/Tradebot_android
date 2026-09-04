package ru.bolotov.tradebot

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import ru.bolotov.tradebot.notifications.FcmTokenRegistrar
import ru.bolotov.core.dependency.DependenciesMap
import ru.bolotov.core.dependency.HasDependencies
import ru.bolotov.tradebot.di.components.DaggerAppComponent
import ru.bolotov.tradebot.router.Destinations
import javax.inject.Inject

class TradebotApplication : Application(), HasDependencies, SingletonImageLoader.Factory  {
    @Inject
    override lateinit var dependenciesMap: DependenciesMap

    @Inject
    override lateinit var dependenciesNav: Destinations

    @Inject
    lateinit var fcmTokenRegistrar: FcmTokenRegistrar

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
        FirebaseMessaging.getInstance().token.addOnSuccessListener(fcmTokenRegistrar::register)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            /*.components {
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
            }*/
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
