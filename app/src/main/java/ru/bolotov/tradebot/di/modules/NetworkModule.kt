package ru.bolotov.tradebot.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.OkHttpClient
import ru.bolotov.core.network.NetworkApiProvider
import ru.bolotov.core.network.StompTradingStateInteractor
import ru.bolotov.core.network.TradingStateInteractor
import ru.bolotov.feature.auth.api.AuthApi
import ru.bolotov.feature.auth.data.*
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

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BackendUrl

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BackendProvider

@Module(includes = [
    BotApiModule::class, EmergencyCloseApiModule::class, RiskApiModule::class,
    InstrumentsApiModule::class, NotificationsApiModule::class, PositionsApiModule::class, StrategyApiModule::class
])
internal class NetworkModule {
    @Provides @BackendUrl
    fun provideBackendUrl(): String = BuildConfig.BACKEND_URL

    @Suppress("DEPRECATION")
    @Provides @Singleton
    fun provideEncryptedSharedPreferences(context: Context): SharedPreferences {
        val key = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(context, "secure_cookies_prefs", key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    @Provides @Singleton
    fun provideJson() = Json {
        explicitNulls = false
        encodeDefaults = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule { contextual(Instant::class, InstantSerializer) }
    }

    /** No auth interceptor, automatic retries, body logger, or inspection tool on credential requests. */
    @Provides @Singleton
    fun authApi(@BackendUrl backendUrl: String, json: Json): AuthApi {
        val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).callTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false).build()
        return NetworkProvider(backendUrl, json, client).provideApiService(AuthApi::class)
    }

    @Provides @Singleton
    fun tokenStore(preferences: SharedPreferences, json: Json) = SecureTokenStore(preferences, json)

    @Provides @Singleton
    fun authRepository(api: AuthApi, store: SecureTokenStore) = AuthRepositoryImpl(api, store)

    @Provides @Singleton
    fun providesNetworkProvider(@BackendUrl backendUrl: String, json: Json, client: OkHttpClient): NetworkApiProvider =
        NetworkProvider(backendUrl, json, client)

    @Provides @Singleton
    fun provideTradingStateInteractor(@BackendUrl backendUrl: String, json: Json, client: OkHttpClient): TradingStateInteractor =
        StompTradingStateInteractor(
            client = client, json = json,
            webSocketUrl = backendUrl.substringBefore("/api/").replaceFirst("https://", "wss://")
                .replaceFirst("http://", "ws://") + "/ws/realtime"
        )

    @Provides @Singleton
    fun providesOkHttpClient(sessions: AuthRepositoryImpl): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS).callTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HeadersInterceptor()).addInterceptor(BearerInterceptor(sessions))
        .authenticator(BearerAuthenticator(sessions)).build()
}
