package ru.bolotov.service.network

import java.io.IOException

class NetworkException(
    override val message: String?,
    override val cause: Throwable,
) : Exception(message, cause)

class ExceptionWithCode(
    override val message: String?,
    val code: Int?,
    override val cause: Throwable,
) : Exception(message, cause)

class NoNetworkException(
    override val message: String?,
    override val cause: Throwable
) : Exception(message, cause)

class ApiErrorException(val code: String?, override val message: String) : IOException(message)