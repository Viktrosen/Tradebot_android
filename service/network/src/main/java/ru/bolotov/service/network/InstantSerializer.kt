package ru.bolotov.service.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object InstantSerializer : KSerializer<Instant> {
    // Форматтер для вашего формата даты
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        val localDateTime = LocalDateTime.ofInstant(value, ZoneOffset.UTC)
        encoder.encodeString(localDateTime.format(formatter))
    }

    override fun deserialize(decoder: Decoder): Instant {
        val dateString = decoder.decodeString()

        // Вариант 1: Если дата в UTC
        val localDateTime = LocalDateTime.parse(dateString, formatter)
        return localDateTime.toInstant(ZoneOffset.UTC)

        // Вариант 2: Если дата в системной временной зоне
        // return localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    }
}