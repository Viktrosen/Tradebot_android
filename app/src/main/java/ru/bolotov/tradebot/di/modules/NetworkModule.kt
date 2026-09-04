package ru.bolotov.tradebot.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.bolotov.core.network.NetworkApiProvider
import ru.bolotov.core.network.StompTradingStateInteractor
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.dashboard.api.BotApiModule
import ru.bolotov.feature.more.api.EmergencyCloseApiModule
import ru.bolotov.feature.risk.api.RiskApiModule
import ru.bolotov.feature.instruments.api.InstrumentsApiModule
import ru.bolotov.feature.notifications.api.NotificationsApiModule
import ru.bolotov.feature.positions.api.PositionsApiModule
import ru.bolotov.feature.strategy.api.StrategyApiModule
import ru.bolotov.service.network.InstantSerializer
import ru.bolotov.service.network.NetworkProvider
import ru.bolotov.tradebot.BuildConfig
import ru.bolotov.tradebot.utils.HeadersInterceptor
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


/**
 * Таймаут соединения
 */
private const val OKHTTP_CONNECT_TIMEOUT = 0L

/**
 * Таймаут записи
 */
private const val OKHTTP_WRITE_TIMEOUT = 0L

/**
 * Таймаут чтения
 */
private const val OKHTTP_READ_TIMEOUT = 0L

/**
 * Таймаут на весь запрос
 */
private const val OKHTTP_CALL_TIMEOUT = 60L

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BackendUrl

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BackendProvider

@Module(
    includes = [
        BotApiModule::class,
        EmergencyCloseApiModule::class,
        RiskApiModule::class,
        InstrumentsApiModule::class,
        NotificationsApiModule::class,
        PositionsApiModule::class
        , StrategyApiModule::class
    ]
)
internal class NetworkModule {

    @Provides
    @BackendUrl
    fun provideBackendUrl(): String = BuildConfig.BACKEND_URL

    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKey.DEFAULT_MASTER_KEY_ALIAS

        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            /* fileName = */ "secure_cookies_prefs",
            /* masterKeyAlias = */
            masterKeyAlias,
            /* context = */
            context,
            /* prefKeyEncryptionScheme = */
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            /* prefValueEncryptionScheme = */
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideJson() = Json {
        isLenient = true
        prettyPrint = true
        explicitNulls = false
        encodeDefaults = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(Instant::class, InstantSerializer)
        }
    }

    @Provides
    @Singleton
    fun providesNetworkProvider(
        @BackendUrl backendUrl: String,
        json: Json,
        okHttpClient: OkHttpClient
    ): NetworkApiProvider = NetworkProvider(
        backendUrl = backendUrl,
        json = json,
        okHttpClient = okHttpClient
    )

    @Provides
    @Singleton
    fun provideTradingStateInteractor(
        @BackendUrl backendUrl: String,
        json: Json,
        okHttpClient: OkHttpClient
    ): TradingStateInteractor = StompTradingStateInteractor(
        client = okHttpClient,
        json = json,
        webSocketUrl = backendUrl
            .substringBefore("/api/")
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://") + "/ws/realtime"
    )

    @Provides
    @Singleton
    fun providesOkHttpClient(
        context: Context,
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            //.followSslRedirects(false)
            .connectTimeout(OKHTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(OKHTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(OKHTTP_CALL_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(
                HeadersInterceptor()
            )
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(ChuckerInterceptor(context))
                    addInterceptor(
                        HttpLoggingInterceptor { message ->
                            Log.d("NetworkCall", message)
                        }.apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    hostnameVerifier { _, _ -> true }
                }
            }.build()
    }
}
