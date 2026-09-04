package ru.bolotov.core.network

import androidx.annotation.Keep
import kotlin.reflect.KClass

@Keep
interface NetworkApiProvider {
    fun <S : Any> provideApiService(serviceClass: KClass<S>): S
}