package ru.bolotov.core.utils

import kotlinx.coroutines.flow.Flow

interface UserToken {

    val navigateToAuthorizationScreen: Flow<String?>

    /**
     * Получить токен пользователя.
     *
     * @return Flow<String>
     */
    fun fetchToken(): Flow<String>

    /**
     * Сохранить токен пользователя.
     *
     * @param token Токен для сохранения
     */
    suspend fun saveTokens(token: String)

    /**
     * Удалить токены пользователя.
     */
    suspend fun deleteTokens(alertMessage: String?)
}