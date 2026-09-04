package ru.bolotov.core.utils

sealed class Entity<out T> {

    data class Success<out T>(
        val data: T,
    ) : Entity<T>()

    data class Error(
        val code: String = "",
        val message: String = "Error"
    ) : Entity<Nothing>()
}

suspend fun <T, R> Entity<T>.onSuccess(block: suspend (T) -> R): Entity<T> {
    if (this is Entity.Success) {
        Entity.Success(block(this.data))
    }
    return this
}

suspend fun <T> Entity<T>.onError(block: suspend (String, String) -> Unit) {
    if (this is Entity.Error) {
        block(this.code, this.message)
    }
}