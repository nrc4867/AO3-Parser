package dev.chieppa.wrapper.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.security.InvalidKeyException
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.*

private val weekBasedYear = WeekFields.SUNDAY_START.weekBasedYear()
private val AO3WrapperSupportedAccessors = setOf(ChronoField.DAY_OF_MONTH, ChronoField.MONTH_OF_YEAR, weekBasedYear)

private class AO3TemporalAccessor(val day: Long, val month: Long, val year: Long): TemporalAccessor {

    override fun isSupported(field: TemporalField?): Boolean {
        return AO3WrapperSupportedAccessors.contains(field)
    }

    override fun getLong(field: TemporalField?): Long {
        return when(field) {
            ChronoField.DAY_OF_MONTH -> day
            ChronoField.MONTH_OF_YEAR -> month
            weekBasedYear -> year
            else -> {0}
        }
    }
}

class TemporalFieldSerializer : KSerializer<TemporalField> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Field", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TemporalField {
        return when(val field = decoder.decodeString()) {
            "DAY" -> ChronoField.DAY_OF_MONTH
            "MONTH" -> ChronoField.MONTH_OF_YEAR
            "YEAR" -> weekBasedYear
            else -> throw InvalidKeyException(field)
        }
    }

    override fun serialize(encoder: Encoder, value: TemporalField) {
        when(value) {
            ChronoField.DAY_OF_MONTH -> encoder.encodeString("DAY")
            ChronoField.MONTH_OF_YEAR -> encoder.encodeString("MONTH")
            weekBasedYear -> encoder.encodeString("YEAR")
            else -> encoder.encodeString("UNKNOWN_${value.getDisplayName(Locale.ENGLISH)}")
        }
    }

}

class TemporalAccessorSerializer : KSerializer<TemporalAccessor> {
    private val delegateDescriptor = MapSerializer(TemporalFieldSerializer(), Int.serializer())
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("TemporalAccessor", delegateDescriptor.descriptor)


    override fun deserialize(decoder: Decoder): TemporalAccessor {
        val decoded = decoder.decodeSerializableValue(delegateDescriptor)
        return AO3TemporalAccessor(
            day = decoded[ChronoField.DAY_OF_MONTH]?.toLong() ?: 1,
            month = decoded[ChronoField.MONTH_OF_YEAR]?.toLong() ?: 1,
            year = decoded[weekBasedYear]?.toLong() ?: 2000
        )
    }

    override fun serialize(encoder: Encoder, value: TemporalAccessor) {

        val encoding = mapOf<TemporalField, Int>(
            Pair(ChronoField.DAY_OF_MONTH, value.get(ChronoField.DAY_OF_MONTH)),
            Pair(ChronoField.MONTH_OF_YEAR, value.get(ChronoField.MONTH_OF_YEAR)),
            Pair(weekBasedYear, value.get(weekBasedYear))
        )

        encoder.encodeSerializableValue(delegateDescriptor, encoding)

    }
}