package ru.bolotov.service.network

import android.util.Log
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import ru.bolotov.core.utils.Entity
import java.net.UnknownHostException

open class BaseRepository(private val tag: String = TAG) {
    protected suspend fun <K : Any> safeApiSuspendResult(
        call: suspend () -> Response<K>?
    ): ResponseStatus<K> {
        val response: Response<K>?
        val errorModel: DtoError.ErrorResponse?

        return try {
            response = call.invoke()
            if (response != null && response.isSuccessful) {
                return ResponseStatus.Success(response.body())
            }
            val errorBody = response?.errorBody()?.string()
            errorModel = errorBody?.let {
                Json.decodeFromString<DtoError.ErrorResponse>(it)
            }
            return ResponseStatus.Error(
                ExceptionWithCode(
                    message = errorModel?.message,
                    code = errorModel?.code,
                    cause = Throwable(errorBody)
                )
            )
        } catch (e: Exception) {
            Log.e(tag, e.localizedMessage ?: ERROR_MESSAGE)

            when (e) {
                is ApiErrorException ->
                    ResponseStatus.Error(
                        ExceptionWithCode(
                            message = e.message,
                            code = e.code?.toInt(),
                            cause = Throwable(e)
                        )
                    )

                is UnknownHostException -> ResponseStatus.Error(
                    NetworkException(e.message, Throwable(e))
                )

                is HttpException -> {
                    ResponseStatus.Error(
                        NetworkException(
                            e.response()?.errorBody()?.toString() ?: "",
                            Throwable(e)
                        )
                    )
                }

                else -> {
                    ResponseStatus.Error(
                        NoNetworkException(null, Throwable(e))
                    )
                }
            }
        }
    }

    protected fun <G : Any> map(call: () -> G): Entity<G> {
        return try {
            Entity.Success(
                call.invoke()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Entity.Error(ERROR_MESSAGE)
        }
    }


    protected fun <T : Any, R : Any> ResponseStatus<T>.asEntity(
        data: R? = null,
        error: String = BASE_ERROR_MESSAGE
    ): Entity<R> = when (this) {
        is ResponseStatus.Success -> data?.let { Entity.Success(it) } ?: Entity.Error(error)
        is ResponseStatus.Error -> Entity.Error(this.exception.message ?: error)
    }

    companion object {
        private const val TAG = "Repository"
        const val ERROR_MESSAGE = "Произошла ошибка"
        private const val BASE_ERROR_MESSAGE = "Произошла ошибка"
    }
}