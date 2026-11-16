package ru.bolotov.tradebot.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.bolotov.core.network.NetworkApiProvider
import ru.bolotov.core.network.SharedTradingStateInteractor
import ru.bolotov.core.network.StompTradingStateInteractor
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.auth.api.AuthApi
import ru.bolotov.feature.auth.data.local.SecureTokenStore
import ru.bolotov.feature.auth.data.network.BearerAuthenticator
import ru.bolotov.feature.auth.data.network.BearerInterceptor
import ru.bolotov.feature.auth.data.repository.AuthRepositoryImpl
import ru.bolotov.feature.dashboard.api.BotApiModule
import ru.bolotov.feature.instruments.api.InstrumentsApiModule
import ru.bolotov.feature.more.api.EmergencyCloseApiModule
import ru.bolotov.feature.notifications.api.NotificationsApiModule
import ru.bolotov.feature.positionDetails.api.di.PositionDetailsApiModule
import ru.bolotov.feature.positions.api.PositionsApiModule
import ru.bolotov.feature.risk.api.RiskApiModule
import ru.bolotov.feature.strategy.api.StrategyApiModule
import ru.bolotov.service.network.InstantSerializer
import ru.bolotov.service.network.NetworkProvider
import ru.bolotov.tradebot.BuildConfig
import ru.bolotov.tradebot.utils.HeadersInterceptor
import ru.bolotov.tradebot.utils.createHttpLoggingInterceptor

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class BackendUrl

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class BackendProvider

@Module(
    includes =
        [
            BotApiModule::class,
            EmergencyCloseApiModule::class,
            RiskApiModule::class,
            InstrumentsApiModule::class,
            NotificationsApiModule::class,
            PositionsApiModule::class,
            StrategyApiModule::class,
            PositionDetailsApiModule::class,
        ]
)
internal class NetworkModule {
    @Provides @BackendUrl fun provideBackendUrl(): String = BuildConfig.BACKEND_URL

    @Suppress("DEPRECATION")
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(context: Context): SharedPreferences {
        val key = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
            context,
            "secure_cookies_prefs",
            key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    @Provides
    @Singleton
    fun provideJson() = Json {
        explicitNulls = false
        encodeDefaults = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule { contextual(Instant::class, InstantSerializer) }
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        createHttpLoggingInterceptor(BuildConfig.DEBUG) { message ->
            Log.d("TradebotHttp", message)
        }

    /** Separate client without auth recursion/retries; debug logging contains metadata only. */
    @Provides
    @Singleton
    fun authApi(
        @BackendUrl backendUrl: String,
        json: Json,
        logging: HttpLoggingInterceptor,
    ): AuthApi {
        val client =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .callTimeout(45, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(logging)
                .build()
        return NetworkProvider(backendUrl, json, client).provideApiService(AuthApi::class)
    }

    @Provides
    @Singleton
    fun tokenStore(preferences: SharedPreferences, json: Json) = SecureTokenStore(preferences, json)

    @Provides
    @Singleton
    fun authRepository(api: AuthApi, store: SecureTokenStore) = AuthRepositoryImpl(api, store)

    @Provides
    @Singleton
    fun providesNetworkProvider(
        @BackendUrl backendUrl: String,
        json: Json,
        client: OkHttpClient,
    ): NetworkApiProvider = NetworkProvider(backendUrl, json, client)

    @Provides
    @Singleton
    fun provideTradingStateInteractor(
        @BackendUrl backendUrl: String,
        json: Json,
        client: OkHttpClient,
    ): TradingStateInteractor =
        SharedTradingStateInteractor(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            source =
                StompTradingStateInteractor(
                    client = client,
                    json = json,
                    webSocketUrl =
                        backendUrl
                            .substringBefore("/api/")
                            .replaceFirst("https://", "wss://")
                            .replaceFirst("http://", "ws://") + "/ws/realtime",
                ),
        )

    @Provides
    @Singleton
    fun providesOkHttpClient(
        sessions: AuthRepositoryImpl,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HeadersInterceptor())
            .addInterceptor(BearerInterceptor(sessions))
            .addInterceptor(logging)
            .authenticator(BearerAuthenticator(sessions))
            .build()
}
