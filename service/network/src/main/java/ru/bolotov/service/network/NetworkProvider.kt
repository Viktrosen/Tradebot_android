package ru.bolotov.service.network

import androidx.annotation.Keep
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.bolotov.core.network.NetworkApiProvider
import kotlin.reflect.KClass

@Keep
class NetworkProvider(
    private val backendUrl: String,
    private val json: Json,
    private val okHttpClient: OkHttpClient
) : NetworkApiProvider {

    override fun <S : Any> provideApiService(serviceClass: KClass<S>): S {
        return provideRetrofit(
            baseUrl = backendUrl,
            okHttpClient = okHttpClient,
        ).create(serviceClass.java)
    }

    private fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val gsonConverterFactory = json.asConverterFactory("application/json".toMediaType())
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

}