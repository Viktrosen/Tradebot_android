package ru.bolotov.service.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface DtoError {

    @Serializable
    data class ErrorResponse(
        @SerialName("name") val name: String? = "",
        @SerialName("message") val message: String? = "",
        @SerialName("code") val code: Int? = null,
        @SerialName("status") val status: Int? = null,
        @SerialName("type") val type: String? = "",
    )

}